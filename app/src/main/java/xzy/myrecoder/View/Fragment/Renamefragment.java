package xzy.myrecoder.View.Fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import xzy.GreebDao.RecoderItemDao;
import xzy.myrecoder.Model.RecoderItem;
import xzy.myrecoder.R;
import xzy.myrecoder.DaoHelper.GreenDaoHelper;
import xzy.myrecoder.Tool.FileTool;


/**
 * A simple {@link Fragment} subclass.
 */
public class Renamefragment extends DialogFragment {

    private Button rename_cancleBtn, rename_confirmBtn;
    private EditText renameText;
    private RecoderItemDao recoderItemDao = GreenDaoHelper.getDaoSession().getRecoderItemDao();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rename, container, false);

        rename_cancleBtn=view.findViewById(R.id.rename_cancleBtn);
        rename_confirmBtn=view.findViewById(R.id.rename_confirmBtn);
        renameText = view.findViewById(R.id.filename_textView);
        Bundle bundle = getArguments();
        final Long id = bundle.getLong("id");

        renameText.setText( recoderItemDao.load(id).getItemName().substring(0,recoderItemDao.load(id).getItemName().length()-4));//显示原文件名

//点击取消按钮，窗体消失
        rename_cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Renamefragment.this.dismiss();
            }
        });

        rename_confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( renameText.getText().toString().equals(""))
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("错误");
                    dialog.setMessage("文件名不能为空");
                    dialog.show();
                }
                else{
                    RecoderItem recoderItem = recoderItemDao.load(id);
                    String oldname = recoderItem.getItemName();
                    String newname = renameText.getText().toString()+".amr";
                    //本地文件修改名称
                    int result = FileTool.renameFile(oldname,newname);
                    if (result==1)
                    {
                        //数据库中修改文件名称
                        recoderItem.setItemName(newname);
                        recoderItemDao.update(recoderItem);
                        Toast.makeText(getActivity(), oldname+" 成功命名为\n "+newname, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"命名已存在，请重新命名",Toast.LENGTH_SHORT).show();
                    }
                    Renamefragment.this.dismiss();
                }

            }
        });

        return view;
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

