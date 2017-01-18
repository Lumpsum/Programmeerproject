package mprog.nl.programmeerproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Rick on 1/18/2017.
 */

public class UserRequestAdapter extends ArrayAdapter<UserReqestItem>  {
    Context context;
    ArrayList<UserReqestItem> arrayList;

    public UserRequestAdapter(Context context, ArrayList<UserReqestItem> arrayList) {
        super(context, R.layout.list_item, arrayList);

        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.user_request_item, parent, false);

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
