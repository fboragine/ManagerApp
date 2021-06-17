package it.uniba.di.sms2021.managerapp.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.SpecsFile;

public class FileListAdapter extends BaseAdapter {

    private final List<SpecsFile> item;
    public ArrayList<Integer> selectedItem;
    Context context;

    public FileListAdapter(Context p_context,List<SpecsFile> p_item) {
        context=p_context;
        item=p_item;
        selectedItem = new ArrayList<>();
    }

    @Override
    public int getCount() { return item.size(); }

    @Override
    public SpecsFile getItem(int position) { return item.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.layout_file, null);
            viewHolder = new ViewHolder();
            viewHolder.fileName = (TextView) view.findViewById(R.id.file_name);
            viewHolder.fileDate = (TextView) view.findViewById(R.id.file_date);
            viewHolder.fileSize = (TextView) view.findViewById(R.id.file_size);
            viewHolder.fileIcon = (ImageView) view.findViewById(R.id.file_icon);
            viewHolder.fileCb = (CheckBox) view.findViewById(R.id.file_cb);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = ((ViewHolder) view.getTag());
        }

        viewHolder.fileName.setText(item.get(position).getNome());
        viewHolder.fileIcon.setImageResource(setFileImageType(new File(item.get(position).getFormato())));
        viewHolder.fileDate.setText(item.get(position).getData());
        viewHolder.fileSize.setText(item.get(position).getDimensione() + " Kb");
        viewHolder.fileCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItem.add(position);
            } else {
                selectedItem.remove((Integer) position);
            }
        });
        return view;
    }

    static class ViewHolder
    {
        CheckBox fileCb;
        ImageView fileIcon;
        TextView fileName;
        TextView fileDate;
        TextView fileSize;
    }

    private int setFileImageType(File file)
    {
        int firstIndex = file.getAbsolutePath().indexOf("/");
        int lastIndex = file.getAbsolutePath().indexOf("/",1);
        String filepath = file.getAbsolutePath();

        if (file.isDirectory())
            return R.drawable.ic_folder_open;
        else
        {
            if (filepath.substring(firstIndex,lastIndex).equalsIgnoreCase("/image")) {
                return R.drawable.ic_image;
            }
            else {
                return R.drawable.ic_file;
            }
        }
    }

}
