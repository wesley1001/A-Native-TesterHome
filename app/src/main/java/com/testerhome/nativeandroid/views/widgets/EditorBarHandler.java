package com.testerhome.nativeandroid.views.widgets;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.MarkDownPreviewActivity;
import com.testerhome.nativeandroid.views.dialog.DialogUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditorBarHandler {

    private Context context;
    private EditText edtContent;
    private InputMethodManager imm;

    public EditorBarHandler(Context context, View editorBar, EditText edtContent) {
        this.context = context;
        this.edtContent = edtContent;
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        ButterKnife.bind(this, editorBar);
    }

    /**
     * 加粗
     */
    @OnClick(R.id.editor_bar_btn_format_bold)
    protected void onBtnFormatBoldClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "**string**");
        edtContent.setSelection(edtContent.getSelectionEnd() - 8, edtContent.getSelectionEnd() - 2);
        imm.showSoftInput(edtContent, 0);
    }

    /**
     * 倾斜
     */
    @OnClick(R.id.editor_bar_btn_format_italic)
    protected void onBtnFormatItalicClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "*string*");
        edtContent.setSelection(edtContent.getSelectionEnd() - 7, edtContent.getSelectionEnd() - 1);
        imm.showSoftInput(edtContent, 0);
    }

    /**
     * 引用
     */
    @OnClick(R.id.editor_bar_btn_format_quote)
    protected void onBtnFormatQuoteClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n> ");
        edtContent.setSelection(edtContent.getSelectionEnd());
    }

    /**
     * 无序列表
     */
    @OnClick(R.id.editor_bar_btn_format_list_bulleted)
    protected void onBtnFormatListBulletedClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n- ");
        edtContent.setSelection(edtContent.getSelectionEnd());
    }

    /**
     * 有序列表 FIXME 这里算法需要优化
     */
    @OnClick(R.id.editor_bar_btn_format_list_numbered)
    protected void onBtnFormatListNumberedClick() {
        edtContent.requestFocus();
        // 查找向上最近一个\n
        for (int n = edtContent.getSelectionEnd() - 1; n >= 0; n--) {
            char c = edtContent.getText().charAt(n);
            if (c == '\n') {
                try {
                    int index = Integer.parseInt(edtContent.getText().charAt(n + 1) + "");
                    if (edtContent.getText().charAt(n + 2) == '.' && edtContent.getText().charAt(n + 3) == ' ') {
                        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n" + (index + 1) + ". ");
                        return;
                    }
                } catch (Exception e) {
                    // TODO 这里有问题是如果数字超过10，则无法检测，未来逐渐优化
                }
            }
        }
        // 没找到
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n1. ");
        edtContent.setSelection(edtContent.getSelectionEnd());
    }

    /**
     * 插入代码
     */
    @OnClick(R.id.editor_bar_btn_insert_code)
    protected void onBtnInsertCodeClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), "\n\n```\n\n```\n ");
        edtContent.setSelection(edtContent.getSelectionEnd() - 6);
    }

    /**
     * 插入链接
     */
    @OnClick(R.id.editor_bar_btn_insert_link)
    protected void onBtnInsertLinkClick() {
        DialogUtils.createAlertDialogBuilder(context)
                .setIcon(R.drawable.ic_insert_link_grey600_24dp)
                .setTitle(R.string.add_link)
                .setView(R.layout.dialog_tool_insert_link)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edtTitle = ButterKnife.findById((Dialog) dialog, R.id.dialog_tool_insert_link_edt_title);
                        EditText edtLink = ButterKnife.findById((Dialog) dialog, R.id.dialog_tool_insert_link_edt_link);

                        String insertText = " [" + edtTitle.getText() + "](" + edtLink.getText() + ") ";
                        edtContent.requestFocus();
                        edtContent.getText().insert(edtContent.getSelectionEnd(), insertText);
                    }

                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * 插入图片
     */
    @OnClick(R.id.editor_bar_btn_insert_photo)
    protected void onBtnInsertPhotoClick() {
        edtContent.requestFocus();
        edtContent.getText().insert(edtContent.getSelectionEnd(), " ![Image](http://resource) ");
        edtContent.setSelection(edtContent.getSelectionEnd() - 10, edtContent.getSelectionEnd() - 2);
        imm.showSoftInput(edtContent, 0);
    }

    /**
     * 预览
     */
    @OnClick(R.id.editor_bar_btn_preview)
    protected void onBtnPreviewClick() {
        String content = edtContent.getText().toString();

        content += "\n\n" + "—— 来自TesterHome官方 [安卓客户端](http://fir.im/p9vs)";

        MarkDownPreviewActivity.open(context, content);
    }

}
