package tasktracker.model.elements;

/**
 * TaskTracker
 * 
 * Copyright 2012 Jeanine Bonot, Michael Dardis, Katherine Jasniewski,
 * Jason Morawski
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may 
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */

import java.util.List;
import com.google.gson.Gson;
import android.content.Context;
import android.util.Log;
import tasktracker.controller.DatabaseAdapter;
import tasktracker.model.AccessURL;
import tasktracker.model.NetworkRequestModel;

/**
 * Creates an object to add a Task to Crowdsourcer when passed to ReadFromURL, THEN adds
 * the given task to the local SQL database with Crowdsourcer's returned ID.
 * 
 * Run by creating an instance.
 */
public class RequestDownloadTaskContent implements NetworkRequestModel {
    private Context context;
    //private User user;
    private String requestString;

    static final Gson gson = new Gson();
    /** index of 'content' for objects in database */

    public RequestDownloadTaskContent(Context contex, String id){
        context = contex;
        String command = "action=" + "get" + "&id=" + id;

        requestString = AccessURL.turnCommandIntoURL(command);

        AccessURL access = new AccessURL(this);
        access.execute(getCrowdsourcerCommand());
    }

    public Context getContext(){
        return context;
    }

    public String getCrowdsourcerCommand(){
        return requestString;
    }

    public void runAfterExecution(String line){
        DatabaseAdapter _dbHelper = new DatabaseAdapter(context);
        // Add to SQL server
        _dbHelper.open();

        Task task = new Task("null");
        String taskName, description, date, creator, likes, content, id = "";	//content is not actually unused
        boolean requiresPhoto, requiresText;
        int pos = 0;

        //Get all of the Tasks from the downloaded string
        requiresPhoto = false;
        requiresText = false;
        pos = line.indexOf("{\"summary\":\"", pos);
        pos = pos + "{\"summary\":\"".length();

        taskName = AccessURL.getTag("<Task>", line, pos);

        if (taskName != null){
            //Task found, parse for its summary...
            creator = AccessURL.getTag("<Creator>", line, pos);
            description = AccessURL.getTag("<Description>", line, pos);
            date = AccessURL.getTag("<Date>", line, pos);
            likes = AccessURL.getTag("<Likes>", line, pos);
            id = AccessURL.getTag(",\"id\":\"", line, pos);
            if ("true".equalsIgnoreCase(AccessURL.getTag("<RequiresPhoto>", line, pos)))
                requiresPhoto = true;
            if ("true".equals(AccessURL.getTag("<RequiresText>", line, pos))) 
                requiresText = true;
            //Create object...
            if (creator != null
                    && description != null
                    && date != null
                    && likes != null){
                //TODO retrieve the user's name via their user ID from the Crowdsourcer users list
                task = new Task(creator);
                task.setName(taskName);
                task.setDescription(description);
                task.setID(id);
                task.setIsDownloaded("No");
                task.setDate(date);
                task.setPhotoRequirement(requiresPhoto);
                task.setTextRequirement(requiresText);
                task.setLikes(Integer.parseInt(likes));
                //TODO set other members? Is this relevent for a downloaded task?
                task.setOtherMembers("");
                task.setIsPrivate(false);
                //Add to local SQL database
                addNewTask(task, task.getCreator(), _dbHelper);
            }

            pos = line.indexOf("\"content\":\"", pos);
            pos = pos + "\"content\":\"".length();
            content = line.substring(pos);
        }
        
        //TODO refresh current page (TaskListView?)

        _dbHelper.close();
    }

    private void addNewTask(Task task, String _user, DatabaseAdapter _dbHelper){
        // Add to SQL server
        _dbHelper.open();
        long rowId = _dbHelper.createTask(task);
        Log.d("RequestDownloadTaskContent", "Create: " + rowId);

    }

}