package mprog.nl.programmeerproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mprog.nl.programmeerproject.R;
import mprog.nl.programmeerproject.Classes.UserReqestItem;

/**
 * Adapter class that handles the user request list items.
 */
public class UserRequestAdapter extends ArrayAdapter<UserReqestItem>  {
    Context context;
    ArrayList<UserReqestItem> arrayList;

    public UserRequestAdapter(Context context, ArrayList<UserReqestItem> arrayList) {
        super(context, R.layout.list_item, arrayList);

        this.context = context;
        this.arrayList = arrayList;
    }

    // Overrides the getView method and allows for more textviews per listitem
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.user_request_item, parent, false);

        // Assign to the xml elements and init the variables
        TextView userName = (TextView) view.findViewById(R.id.userNameText);
        TextView data = (TextView) view.findViewById(R.id.dataText);
        TextView ageGender = (TextView) view.findViewById(R.id.ageGenderText);
        TextView description = (TextView) view.findViewById(R.id.descriptionText);

        userName.setText(arrayList.get(position).getUserName());
        data.setText(arrayList.get(position).getData());
        ageGender.setText(arrayList.get(position).getAge() + ", " + arrayList.get(position).getGender());
        description.setText("Description: " + arrayList.get(position).getDescription());

        return view;
    }
}
