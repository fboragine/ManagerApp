package it.uniba.di.sms2021.managerapp.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms2021.managerapp.R;

public class FileListAdapter extends BaseAdapter {

    private List<String> item;
    private List<String> path;
    public ArrayList<Integer> selectedItem;
    Context context;
    Boolean isRoot;

    public FileListAdapter(Context context, List<String> item, List<String> path, Boolean isRoot) {
        this.context = context;
        this.item = item;
        this.path = path;
        this.isRoot = isRoot;
        this.selectedItem = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.layout_file, null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView) view.findViewById(R.id.file_name);
            viewHolder.fileDate = (TextView) view.findViewById(R.id.file_date);
            viewHolder.fileIcon = (ImageView) view.findViewById(R.id.file_icon);
            viewHolder.fileCb = (CheckBox) view.findViewById(R.id.file_cb);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = ((ViewHolder) view.getTag());
        }
        if (!isRoot && position == 0) {
            viewHolder.fileCb.setVisibility(View.INVISIBLE);
        }

        viewHolder.fileName.setText(item.get(position));
        viewHolder.fileIcon.setImageResource(setFileImageType(new File(path.get(position))));
        viewHolder.fileDate.setText(getLastDate(position));
        viewHolder.fileCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItem.add(position);
                } else {
                    selectedItem.remove(selectedItem.indexOf(position));
                }
            }
        });

        return view;
    }


    class ViewHolder
    {
        CheckBox fileCb;
        ImageView fileIcon;
        TextView fileName;
        TextView fileDate;
    }

    private int setFileImageType(File file)
    {
        int lastIndex=file.getAbsolutePath().lastIndexOf(".");
        String filepath=file.getAbsolutePath();
        if (file.isDirectory())
            return R.drawable.ic_folder_open;
        else
        {
            if(filepath.substring(lastIndex).equalsIgnoreCase(".png") || filepath.substring(lastIndex).equalsIgnoreCase(".jpg"))
            {
                return R.drawable.ic_image;
            }
            else
            {
                return R.drawable.ic_file;
            }
        }
    }

    String getLastDate(int position)
    {
        File file=new File(path.get(position));
        SimpleDateFormat m_dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return m_dateFormat.format(file.lastModified());
    }
}
