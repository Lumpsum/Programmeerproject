package mprog.nl.programmeerproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mprog.nl.programmeerproject.Classes.ListItem;
import mprog.nl.programmeerproject.R;

/**
 * Adapter class that handles the chat messages send by users.
 */
public class ChatListAdapter extends ArrayAdapter<ListItem> {
    Context context;
    ArrayList<ListItem> arrayList;

    public ChatListAdapter(Context context, ArrayList<ListItem> arrayList) {
        super(context, R.layout.list_item, arrayList);

        this.context = context;
        this.arrayList = arrayList;
    }

    // Overrides the getView method in order for the listview to allow for more than one item.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_item, parent, false);

        // Assign to the xml elements and init the variables
        TextView userName = (TextView) view.findViewById(R.id.userNameText);
        TextView data = (TextView) view.findViewById(R.id.dataText);

        userName.setText(arrayList.get(position).getUserName());
        data.setText(arrayList.get(position).getData());

        return view;
    }
}
