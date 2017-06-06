package com.euet.guider.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.euet.guider.R;
import com.euet.library.util.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * Created by xiewensheng on 2016/11/11.
 * 显示相册弹出框的Dialog工具类
 */
public class CommonDialogUtils {

    /**
     * 从底下弹三个框
     *
     * @param context
     * @param stringItems
     * @return
     */
    public static Dialog showListDilog(Context context, final String[] stringItems, final OnDialogItemClickListener itemClickListener) {

        final Dialog dialog = new Dialog(context, R.style.MyDialogTheme);
        dialog.setContentView(R.layout.layout_dialog_list);

        LinearLayout ll_dialog = (LinearLayout) dialog.findViewById(R.id.ll_dialog);
        Button bnt_cancel = (Button) dialog.findViewById(R.id.bnt_cancel);

        for (int i = 0; i < stringItems.length; i++) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_dialog, null);
            TextView bnt_item = (TextView) itemView.findViewById(R.id.bnt_item);
            View line = (View) itemView.findViewById(R.id.line);
            final int finalI = i;
            bnt_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClickPositon(finalI);
                        dialog.dismiss();
                    }
                }
            });
            if (i == stringItems.length - 1) {
                line.setVisibility(View.INVISIBLE);
                bnt_item.setBackgroundResource(R.drawable.shap_bg_white_dowm);
            } else if (i == 0) {
                bnt_item.setBackgroundResource(R.drawable.shap_bg_white);
            } else {
                bnt_item.setBackgroundResource(R.drawable.shap_bg_white_sec1);
            }

            bnt_item.setText(stringItems[i]);
            ll_dialog.addView(itemView);
        }
        ll_dialog.requestLayout();

        dialog.findViewById(R.id.bnt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }


    /**
     * 导游分房 点击自动分房显示的Dialog
     *
     * @param context
     * @param seprateSecDilogListener
     * @return
     */
    public static Dialog showHouseSeprateTypeDilog(Context context, final OnHouseSeprateSecDilogListener seprateSecDilogListener) {
        final Dialog dialog = new Dialog(context, R.style.MyDialogTheme);
        dialog.setContentView(R.layout.layout_dilog_house_seprate);
        RelativeLayout rl_auto = (RelativeLayout) dialog.findViewById(R.id.rl_auto);
        RelativeLayout rl_sex_auto = (RelativeLayout) dialog.findViewById(R.id.rl_sex_auto);
        // RelativeLayout rl_all = (RelativeLayout) dialog.findViewById(R.id.rl_all);
        final ImageView iv_aoto_seprate = (ImageView) dialog.findViewById(R.id.iv_aoto_seprate);
        final ImageView iv_sex_seprate = (ImageView) dialog.findViewById(R.id.iv_sex_seprate);

        TextView bnt_cancel = (TextView) dialog.findViewById(R.id.bnt_cancel);
        TextView bnt_ok = (TextView) dialog.findViewById(R.id.bnt_ok);

        iv_aoto_seprate.setSelected(true);
        iv_sex_seprate.setSelected(false);
        rl_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_aoto_seprate.setSelected(true);
                iv_sex_seprate.setSelected(false);
            }
        });

        rl_sex_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_aoto_seprate.setSelected(false);
                iv_sex_seprate.setSelected(true);
            }
        });

        bnt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bnt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seprateSecDilogListener != null) {
                    int positon = 0;
                    if (iv_aoto_seprate.isSelected()) {
                        positon = 0;
                    }
                    if (iv_sex_seprate.isSelected()) {
                        positon = 1;
                    }
                    seprateSecDilogListener.OnHoseTypeSecClick(positon);
                }
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }


    /**
     * 普通的Dialog
     * @param context
     * @return
     */
    public static Dialog showNomalDialog(Context context, String content, String left, String right, final OnDialogItemClickListener itemClickListener) {
        final Dialog dialog = new Dialog(context, R.style.MyDialogTheme);
        dialog.setContentView(R.layout.layout_dilog_nomal);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        TextView tv_left = (TextView) dialog.findViewById(R.id.tv_left);
        TextView tv_right = (TextView) dialog.findViewById(R.id.tv_right);
        if (!StringUtils.isBlank(content)) {
            tv_content.setText(content);
        }
        if (!StringUtils.isBlank(left)) {
            tv_left.setText(left);
        }
        if (!StringUtils.isBlank(right)) {
            tv_right.setText(right);
        }
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickPositon(0);
                }
                dialog.dismiss();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickPositon(1);
                }
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    /**
     * 一个按钮 并且自定标题
     *
     * @param context
     * @return
     */
    public static Dialog showNomalOnButtonDialog(Context context, String title, String content, String right, final OnDialogItemClickListener itemClickListener) {
        final Dialog dialog = new Dialog(context, R.style.MyDialogTheme);
        dialog.setContentView(R.layout.layout_dilog_nomal3);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        TextView tv_left = (TextView) dialog.findViewById(R.id.tv_left);
        TextView tv_right = (TextView) dialog.findViewById(R.id.tv_right);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);

        tv_left.setVisibility(View.GONE);

        if (!StringUtils.isBlank(content)) {
            tv_content.setText(content);
        }
        if (!StringUtils.isBlank(title)) {
            tv_title.setText(title);
        }

        if (!StringUtils.isBlank(right)) {
            tv_right.setText(right);
        }

        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickPositon(0);
                }
                dialog.dismiss();
            }
        });

        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickPositon(1);
                }
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    /**
     * 没有温馨提示
     *@param type 表示类型 背景颜色进行改变 1：白色  2：灰色
     * @param context
     * @return
     */
    public static Dialog showNomal2Dialog(Context context,int type, String content, String left, String right, final OnDialogItemClickListener itemClickListener) {
        final Dialog dialog = new Dialog(context, R.style.MyDialogTheme);
        dialog.setContentView(R.layout.layout_dilog_nomal1);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        TextView tv_left = (TextView) dialog.findViewById(R.id.tv_left);
        TextView tv_right = (TextView) dialog.findViewById(R.id.tv_right);
        LinearLayout ll_main = (LinearLayout) dialog.findViewById(R.id.ll_main);
        View line=dialog.findViewById(R.id.line);
        View view_line1=dialog.findViewById(R.id.view_line1);

        GradientDrawable mGroupDrawable = (GradientDrawable) ll_main.getBackground();
       /*设置整体背景颜色*/
        if(type==1){
            mGroupDrawable.setColor(Color.parseColor("#ffffff"));
            tv_content.setTextColor(Color.parseColor("#000000"));
            tv_left.setTextColor(Color.parseColor("#808080"));

            line.setBackgroundColor(Color.parseColor("#D7D7D7"));
            view_line1.setBackgroundColor(Color.parseColor("#D7D7D7"));

        }else {
            mGroupDrawable.setColor(Color.parseColor("#383838"));
            tv_content.setTextColor(Color.parseColor("#bbffffff"));
            tv_left.setTextColor(Color.parseColor("#bbffffff"));

            line.setBackgroundColor(Color.parseColor("#222222"));
            view_line1.setBackgroundColor(Color.parseColor("#222222"));
        }

        if (!StringUtils.isBlank(content)) {
            tv_content.setText(content);
        }

        if (!StringUtils.isBlank(left)) {
            tv_left.setText(left);
        }

        if (!StringUtils.isBlank(right)) {
            tv_right.setText(right);
        }




        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickPositon(0);
                }
                dialog.dismiss();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickPositon(1);
                }
                dialog.dismiss();
            }
        });


        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    /**
     * 知道了
     *
     * @param context
     * @return
     */
    public static Dialog showOneButtonDialog(Context context, String content, String bntText, final OnDialogItemClickListener itemClickListener) {
        final Dialog dialog = new Dialog(context, R.style.MyDialogTheme);
        dialog.setContentView(R.layout.layout_dilog_nomal2);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        TextView tv_right = (TextView) dialog.findViewById(R.id.tv_one);

        if (!StringUtils.isBlank(content)) {
            tv_content.setText(content);
        }

        if (!StringUtils.isBlank(bntText)) {
            tv_right.setText(bntText);
        }

        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickPositon(0);
                }
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }


    public interface OnHouseSeprateSecDilogListener {
        public void OnHoseTypeSecClick(int positionSec);

    }

    public interface OnCameraDilogListener {
        public void OnCameraClick();

        public void OnPhotoList();

        public void OnCancelLisener();
    }


    public interface OnDialogItemClickListener {
        void onItemClickPositon(int positon);
    }

}
