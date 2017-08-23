package com.earth.data.down;

/**
 * Created by Frapo on 2017/4/22.
 * Earth 16:19
 */

public class TransferManager {
    public void upload() {/*
        //开始上传时将选中的文件路径通过intent传给服务
        list_filePath.clear();
        //将文件的路径传入service
        int file_count = map_check_file.size();
//                L.i("-----count"+file_count);
        if(file_count==0){
            T.showShort(this,"空文件夹不能上传");
            return;
        }

        loading_dialog1.show();
        for(Map.Entry<String, File> entry: map_check_file.entrySet()){
            File value = entry.getValue();
            list_filePath.add(value.getAbsolutePath());
        }
        Intent intent_service = new Intent();
        intent_service.setAction(MainActivity.ACTION_START);
        intent_service.setPackage(getPackageName());
        intent_service.putExtra("file_list", (Serializable) list_filePath);
        getApplication().startService(intent_service);


        // Case tv——lixian
/*
        if (map_check_file.size() != 0) {
            for (Map.Entry<String, File> entry : map_check_file.entrySet()) {
                //将未上传的文件加入数据库
                fileDaoImp.insertData(entry.getValue().getAbsolutePath());
            }
            Fragment fragmentById = manager.findFragmentById(R.id.fl_director_activity);
            FileFragment fileFragment = (FileFragment) fragmentById;
            if (fileFragment != null) {
                sendMessageToFragment(fileFragment);
            }
            T.showShort(this, "已加入离线");
            rl_director_bottom.setVisibility(View.GONE);
        } else {
            T.showShort(this,"空文件夹不能加入离线");
        }
    }*/
    }
}
