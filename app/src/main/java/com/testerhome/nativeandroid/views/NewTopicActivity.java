package com.testerhome.nativeandroid.views;

/**
 * Created by cvter on 8/3/16.
 */

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.models.TopicResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.storage.TopicShared;
import com.testerhome.nativeandroid.utils.ToastUtils;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;
import com.testerhome.nativeandroid.views.dialog.DialogUtils;
import com.testerhome.nativeandroid.views.dialog.ProgressDialog;
import com.testerhome.nativeandroid.views.widgets.EditorBarHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewTopicActivity extends BackBaseActivity {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.new_topic_spn_tab)
    protected Spinner spnTab;

    @BindView(R.id.new_topic_edt_title)
    protected EditText edtTitle;

    @BindView(R.id.editor_bar_layout_root)
    protected ViewGroup editorBar;

    @BindView(R.id.new_topic_edt_content)
    protected EditText edtContent;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        ButterKnife.bind(this);

        dialog = DialogUtils.createProgressDialog(this);
        dialog.setMessage(R.string.posting_$_);
        dialog.setCancelable(false);


        // 创建EditorBar
        new EditorBarHandler(this, editorBar, edtContent);

        //获取保存的信息
        spnTab.setSelection(TopicShared.getNewTopicTabPosition(this));
        edtContent.setText(TopicShared.getNewTopicContent(this));
        edtContent.setSelection(edtContent.length());
        edtTitle.setText(TopicShared.getNewTopicTitle(this));
        edtTitle.setSelection(edtTitle.length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_new, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 实时保存草稿
     */
    @Override
    protected void onPause() {
        super.onPause();
        TopicShared.setNewTopicTabPosition(this, spnTab.getSelectedItemPosition());
        TopicShared.setNewTopicTitle(this, edtTitle.getText().toString());
        TopicShared.setNewTopicContent(this, edtContent.getText().toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            if (edtTitle.length() == 0) {
                edtTitle.requestFocus();
                ToastUtils.with(this).show(R.string.title_empty_error_tip);
            } else if (edtContent.length() == 0) {
                edtContent.requestFocus();
                ToastUtils.with(this).show(R.string.content_empty_error_tip);
            } else {
                createTopic();
            }

        }
        return super.onOptionsItemSelected(item);
    }


    public void createTopic() {
        dialog.show();
        String a[] = getResources().getStringArray(R.array.tab_value);
        int po = spnTab.getSelectedItemPosition();
        String content = edtContent.getText().toString() +
                "\n\n" + "—— 来自TesterHome官方 [安卓客户端](http://fir.im/p9vs)";
        Log.d("new", content);
        try {
            final String encodedData = URLEncoder.encode(content, "UTF-8");
            Log.d("new", getResources().getIntArray(R.array.tab_value)[spnTab.getSelectedItemPosition()] + "");
            TesterUser mTesterHomeAccount = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
            RestAdapterUtils.getRestAPI(this)
                    .createTopic(mTesterHomeAccount.getAccess_token(),
                            getResources().getStringArray(R.array.tab_value)[spnTab.getSelectedItemPosition()],
                            edtTitle.getText().toString(), encodedData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<TopicResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Log.d("newActivity", e.getMessage());
                            ToastUtils.with(NewTopicActivity.this).show(e.getMessage());
                        }

                        @Override
                        public void onNext(TopicResponse topicEntity) {
                            dialog.dismiss();
                            if (topicEntity != null && topicEntity.getTopic() != null) {
                                // 清除草稿 TODO 由于保存草稿的动作在onPause中，并且保存过程是异步的，因此保险起见，优先清除控件数据
                                spnTab.setSelection(0);
                                edtTitle.setText(null);
                                edtContent.setText(null);
                                TopicShared.clearNewTopic(NewTopicActivity.this);
                                // 结束当前并提示
                                finish();
                                ToastUtils.with(NewTopicActivity.this).show(R.string.post_success);
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}