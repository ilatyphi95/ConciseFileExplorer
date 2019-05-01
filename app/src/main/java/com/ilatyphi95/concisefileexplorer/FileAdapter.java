package com.ilatyphi95.concisefileexplorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private final Context mP_context;
    private final List<String> mP_item;
    private final List<String> mP_path;
    private final Boolean mP_isRoot;
    public ArrayList<Integer> m_selectedItem;
    private final LayoutInflater mInflater;

    View.OnClickListener mOnItemClickListener;

    public FileAdapter(Context p_context, List<String> p_item, List<String> p_path, Boolean p_isRoot) {
        mInflater = LayoutInflater.from(p_context);
        mP_context = p_context;
        mP_item = p_item;
        mP_path = p_path;
        mP_isRoot = p_isRoot;
        m_selectedItem = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if(!mP_isRoot && position == 0)
        {
            viewHolder.chkFile.setVisibility(View.INVISIBLE);
        }
        viewHolder.txtFileName.setText(mP_item.get(position));
        viewHolder.txtLasModified.setText(getLastDate(position));
        viewHolder.imgFileType.setImageResource(setFileImageType(new File(mP_path.get(position))));
        viewHolder.chkFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    m_selectedItem.add(position);
                }
                else
                {
                    m_selectedItem.remove(m_selectedItem.indexOf(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mP_item != null)
            return mP_item.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtFileName, txtLasModified;
        public ImageView imgFileType;
        public CheckBox chkFile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFileName = itemView.findViewById(R.id.tv_filename);
            txtLasModified = itemView.findViewById(R.id.tv_date);
            imgFileType = itemView.findViewById(R.id.imageView);
            chkFile = itemView.findViewById(R.id.chk_box);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    private int setFileImageType(File m_file)
    {
        int m_lastIndex=m_file.getAbsolutePath().lastIndexOf(".");
        String m_filepath=m_file.getAbsolutePath();
        if (m_file.isDirectory())
            return R.drawable.openfolder;
        else
        {
            if(m_filepath.substring(m_lastIndex).equalsIgnoreCase(".png"))
            {
                return R.drawable.ic_png;
            }
            else if(m_filepath.substring(m_lastIndex).equalsIgnoreCase(".jpg"))
            {
                return R.drawable.ic_jpeg;
            }
            else
            {
                return R.drawable.file;
            }
        }
    }

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    String getLastDate(int p_pos)
    {
        File m_file=new File(mP_path.get(p_pos));
        SimpleDateFormat m_dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return m_dateFormat.format(m_file.lastModified());
    }
}
