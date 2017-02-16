package com.example.freelancer.remaindy;

import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
* contains a list view and floating action button
* */
public class MainActivity extends AppCompatActivity {
    public ListView todolist;
    public FloatingActionButton create;
    public todolistAdapter todolistAdapter;
    public List<Todoobject>todoobjects=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        create = (FloatingActionButton) findViewById(R.id.create);
        todolist = (ListView) findViewById(R.id.todolist);
        SQLiteStorage sqLiteStorage= SQLiteStorage.getInstance(this);
        todoobjects=sqLiteStorage.getalltodoitems();
        todolistAdapter= new todolistAdapter(todoobjects,this);
        todolist.setAdapter(todolistAdapter);

        /*
            on scroll the last list items overflow icon wont visible becoz of overlapping of
            floating action action button
            so this part hides the fab when list scrolled down and makes it visible when list scrolled up
         */
        todolist.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    create.hide();
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    create.show();
                }
                mLastFirstVisibleItem=firstVisibleItem;

            }
        });
        /*
        * on click of floating action button dialog pops up for entering todolist item
        * on click of done  list updates
        * on click of cancel dialog box will get cancled
         */
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title="";
                final Dialog createtodoitem = new Dialog(MainActivity.this);
                createtodoitem.requestWindowFeature(Window.FEATURE_NO_TITLE);
                createtodoitem.setCanceledOnTouchOutside(false);
                createtodoitem.setCancelable(false);
                createtodoitem.setContentView(R.layout.createtodoitem);
                final TextView headerText = (TextView) createtodoitem
                        .findViewById(R.id.dialog_title);
                final EditText text_dialog_content = (EditText) createtodoitem
                        .findViewById(R.id.group_dialog_edit);
                final TextView donebutton = (TextView) createtodoitem
                        .findViewById(R.id.donebutton);
                final TextView cancelbutton = (TextView) createtodoitem
                        .findViewById(R.id.cancelbutton);
                text_dialog_content.setHint("Enter todo item");
                donebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!text_dialog_content.getText().toString().trim().isEmpty()){
                            String title = text_dialog_content.getText().toString().trim();
                            if (!(title.equals(""))) {

                                createnewtodoitem(title);
                                createtodoitem.dismiss();
                            }


                        }
                        else{
                            Toast.makeText(MainActivity.this, "Please enter Todo item", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                cancelbutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        createtodoitem.cancel();
                    }
                });
                createtodoitem.show();

            }

        });
    }
    /*
    * creates a new todoitem object and stores to database
    * on successfull updation from db scrollbar  moves to the latest created item position
     */
    public void  createnewtodoitem(String title){
        Todoobject todoobject= new Todoobject();
        todoobject.setTitle(title);
        Log.e("title",title);
        SQLiteStorage sqLiteStorage =SQLiteStorage.getInstance(getApplicationContext());
        sqLiteStorage.InsertTodoItem(todoobject);
        List<Todoobject> tosdoitems=sqLiteStorage.getalltodoitems();
        todolistAdapter= new todolistAdapter(tosdoitems,this);
        todolistAdapter.notifyDataSetChanged();
        todolist.setAdapter(todolistAdapter);
        todolist.setSelection(tosdoitems.size()-1);
    }
}
