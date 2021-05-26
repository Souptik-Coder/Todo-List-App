package com.coders.TaskApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.coders.TaskApp.Utils.ScheduleNotification;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.hasExtra("Action_Done")){
           int pos= intent.getIntExtra(TodoData.Position,0);
            Log.e("NAR d",pos+"");
            try {
                new TodoData(context).replace(new JSONObject(TodoData.data.get(pos)).put(TodoData.isChecked, true).toString(),
                        pos);
            } catch (JSONException e) {
                Toast.makeText(context,"An error occurred "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
            ScheduleNotification.cancel(context,intent.getIntExtra(TodoData.Request_Code,0));
        }

        else if(intent.hasExtra("Action_Edit")){
            Intent i=new Intent(context,AddTodoActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(TodoData.Item,intent.getStringExtra(TodoData.Item));
            i.putExtra(TodoData.Position,intent.getIntExtra(TodoData.Position,0));
            context.startActivity(i);

        }
    }
}
