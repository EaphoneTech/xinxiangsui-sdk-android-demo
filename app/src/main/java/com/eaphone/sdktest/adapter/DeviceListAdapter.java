package com.eaphone.sdktest.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.eaphone.sdktest.R;

import java.util.List;

/**
 * @ClassName: DeviceTypeListAdapter
 * @Description: java类作用描述
 * @Author: he lin hua
 * @CreateDate: 2020/7/8 16:50
 * @Version: 1.0
 */


public class DeviceListAdapter extends BaseAdapter {


    private Context mContext;
    private List<BluetoothDevice> data;


    public DeviceListAdapter(Context mContext, List<BluetoothDevice> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前项的Fruit实例
        BluetoothDevice item = (BluetoothDevice) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_device_, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name =  view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        String name = item.getName();
        viewHolder.tv_name.setText(name.substring(6, name.length()-4));
        return view;
    }

    // 内部类
    class ViewHolder{
        TextView tv_name;
    }


    public void addData(BluetoothDevice item){
        data.add(item);
        notifyDataSetChanged();
    }

    public void clearData(){
        data.clear();
        notifyDataSetChanged();
    }
}
