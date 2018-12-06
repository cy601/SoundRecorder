package xzy.myrecoder.View.Fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.Model.RecoderItem;
import xzy.myrecoder.R;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;
import xzy.myrecoder.Tool.FileTool;


/**
 * A simple {@link Fragment} subclass.
 */
public class Deletefragament extends DialogFragment {
    private Button delete_cancleBtn, delete_comfirmBtn;
    private RecoderItemDao recoderItemDao = GreenDaoHelper.getDaoSession().getRecoderItemDao();
    private TextView fileNameTV;
    RecoderItem recoderItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete, container, false);

        delete_cancleBtn = view.findViewById(R.id.delete_cancleBtn);
        delete_comfirmBtn = view.findViewById(R.id.delete_confirmBtn);
        fileNameTV = view.findViewById(R.id.fileNameTV);
        Bundle bundle = getArguments();
        final Long id = bundle.getLong("id");
        recoderItem = recoderItemDao.load(id);
        fileNameTV.setText(recoderItem.getItemName());


        //取消按键，点击窗口消失
        delete_cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Deletefragament.this.dismiss();
            }
        });

        delete_comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除本地文件
                FileTool.deleteFile(recoderItem.getItemName());
                //删除数据库中的文件信息
                recoderItemDao.deleteByKey(id);
                //刷新ListActivity
                Toast.makeText(getActivity(), "成功删除 "+recoderItem.getItemName(), Toast.LENGTH_SHORT).show();
                deleteDialogListener deleteDialogListener = (deleteDialogListener) getActivity();
                deleteDialogListener.refresh();
                Deletefragament.this.dismiss();
            }
        });
        return view;
    }

    public interface deleteDialogListener {
        void refresh();
    }


    @Override
    public void onStart() {
        super.onStart();
        //改变fragment宽度占比
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCanceledOnTouchOutside(false);
    }
}