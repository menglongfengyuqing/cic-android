package com.ztmg.cicmorgan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.ztmg.cicmorgan.MyApplication;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.BindBankCardActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.CellEntity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.login.RegisterActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LockPatternUtils;
import com.ztmg.cicmorgan.util.LockPatternView;
import com.ztmg.cicmorgan.util.LockPatternView.Cell;
import com.ztmg.cicmorgan.util.LockPatternView.DisplayMode;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SystemBarTintManager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 启用&修改
 * 手势密码
 */
public class CreateGesturePasswordActivity extends BaseActivity {
    //private TextView lock_reset_tv;
    private TextView tv_jump;
    private String user_id;
    private String password_MD5;
    private String phone;
    private Intent intent;
    private SharedPreferences sp;
    private SharedPreferences mySharedPreferences;
    private boolean isfirst;
    private boolean setFlag;
    private String Type;
    private boolean flag = false;
    private static final String SCENE_LIST = "list";
    private static final int ID_EMPTY_MESSAGE = -1;
    private static final String KEY_UI_STAGE = "uiStage";
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";
    private LockPatternView mLockPatternView;
    protected TextView mHeaderText;
    private TextView tv_dot;
    private Animation mShakeAnim;
    protected List<LockPatternView.Cell> mChosenPattern = null;
    private Toast mToast;
    private Stage mUiStage = Stage.Introduction;
    private View mPreviewViews[][] = new View[3][3];
    List<CellEntity> listCache;
    private DoCacheUtil uitl;
    private String overtime;
    private CellEntity data[] = {new CellEntity(0, 0), new CellEntity(1, 0),
            new CellEntity(2, 0), new CellEntity(0, 1), new CellEntity(1, 1),
            new CellEntity(2, 1), new CellEntity(0, 2), new CellEntity(1, 2),
            new CellEntity(2, 2)};

    /**
     * The patten used during the help screen to show how to draw a pattern.
     */
    private final List<LockPatternView.Cell> mAnimatePattern = new ArrayList<LockPatternView.Cell>();

    /**
     * The states of the left footer button.
     */
    enum LeftButtonMode {
        Cancel(android.R.string.cancel, true),
        CancelDisabled(
                android.R.string.cancel, false),
        Retry(
                R.string.lockpattern_retry_button_text, true),
        RetryDisabled(
                R.string.lockpattern_retry_button_text, false),
        Gone(
                ID_EMPTY_MESSAGE, false);

        /**
         * @param text    The displayed text for this mode.
         * @param enabled Whether the button should be enabled.
         */
        LeftButtonMode(int text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    /**
     * The states of the right button.
     */
    enum RightButtonMode {
        Continue(R.string.lockpattern_continue_button_text, true), ContinueDisabled(
                R.string.lockpattern_continue_button_text, false), Confirm(
                R.string.lockpattern_confirm_button_text, true), ConfirmDisabled(
                R.string.lockpattern_confirm_button_text, false), Ok(
                android.R.string.ok, true);

        /**
         * @param text    The displayed text for this mode.
         * @param enabled Whether the button should be enabled.
         */
        RightButtonMode(int text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    /**
     * Keep track internally of where the user is in choosing a pattern.
     */
    protected enum Stage {

        Introduction(R.string.lockpattern_recording_intro_header,
                LeftButtonMode.Cancel, RightButtonMode.ContinueDisabled,
                ID_EMPTY_MESSAGE, true),
        HelpScreen(
                R.string.lockpattern_settings_help_how_to_record,
                LeftButtonMode.Gone, RightButtonMode.Ok, ID_EMPTY_MESSAGE,
                false),
        ChoiceTooShort(
                R.string.lockpattern_recording_incorrect_too_short,
                LeftButtonMode.Retry, RightButtonMode.ContinueDisabled,
                ID_EMPTY_MESSAGE, true),
        FirstChoiceValid(
                R.string.lockpattern_pattern_entered_header,
                LeftButtonMode.Retry, RightButtonMode.Continue,
                ID_EMPTY_MESSAGE, false),
        NeedToConfirm(
                R.string.lockpattern_need_to_confirm, LeftButtonMode.Cancel,
                RightButtonMode.ConfirmDisabled, ID_EMPTY_MESSAGE, true),
        ConfirmWrong(
                R.string.lockpattern_need_to_unlock_wrong,
                LeftButtonMode.Cancel, RightButtonMode.ConfirmDisabled,
                ID_EMPTY_MESSAGE, true),
        ChoiceConfirmed(
                R.string.lockpattern_pattern_confirmed_header,
                LeftButtonMode.Cancel, RightButtonMode.Confirm,
                ID_EMPTY_MESSAGE, false);

        /**
         * @param headerMessage  The message displayed at the top.
         * @param leftMode       The mode of the left button.
         * @param rightMode      The mode of the right button.
         * @param footerMessage  The footer message.
         * @param patternEnabled Whether the pattern widget is enabled.
         */
        Stage(int headerMessage, LeftButtonMode leftMode,
              RightButtonMode rightMode, int footerMessage,
              boolean patternEnabled) {
            this.headerMessage = headerMessage;
            this.leftMode = leftMode;
            this.rightMode = rightMode;
            this.footerMessage = footerMessage;
            this.patternEnabled = patternEnabled;
        }

        final int headerMessage;
        final LeftButtonMode leftMode;
        final RightButtonMode rightMode;
        final int footerMessage;
        final boolean patternEnabled;
    }

    private void showToast(CharSequence message) {
        if (null == mToast) {
            mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }

        mToast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉信息栏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.white);//通知栏所需颜色
        }
        setContentView(R.layout.creatlocked);

        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(CreateGesturePasswordActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(1000);
        setTitle("设置手势密码");
        setBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(CreateGesturePasswordActivity.this, "314001_szssmm_back_click");
                if (enterType.equals("isRegister")) {
                    uitl.put("cellList", "");
                    startActivity(new Intent(CreateGesturePasswordActivity.this, MainActivity.class));
                    finish();
                } else if (enterType.equals("isModify")) {
                    // String token =
                    // LoginUserProvider.getUser(CreateGesturePasswordActivity.this).getToken().toString();
                    // cancelHandPas(token, "3");
                    uitl.put("cellList", "");
                    finish();
                }
            }
        });
        uitl = DoCacheUtil.get(CreateGesturePasswordActivity.this);
        forgetPWD = getIntent().getBooleanExtra("forgetPWD", false);
        changePWD = getIntent().getStringExtra("locked");
        setFlag = getIntent().getBooleanExtra("set", false);
        enterType = getIntent().getStringExtra("enterType");
        overtime = getIntent().getStringExtra("overtime");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        intent = new Intent(this, MainActivity.class);

        // 初始化演示动画
        mAnimatePattern.add(LockPatternView.Cell.of(0, 0));
        mAnimatePattern.add(LockPatternView.Cell.of(0, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(1, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 1));
        mAnimatePattern.add(LockPatternView.Cell.of(2, 2));
        mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_create_lockview);
        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
        mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);
        tv_dot = (TextView) findViewById(R.id.tv_dot);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        mLockPatternView.setTactileFeedbackEnabled(true);

        // = (TextView) findViewById(R.id.lock_reset_tv);// 登录其他
        tv_jump = (TextView) findViewById(R.id.tv_jump);
        if (setFlag) {
            tv_jump.setVisibility(View.INVISIBLE);
            //lock_reset_tv.setVisibility(View.INVISIBLE);
        }
        initPreviewViews();
        if (savedInstanceState == null) {
            updateStage(Stage.Introduction);
            mLockPatternView.clearPattern();
            updateStage(Stage.Introduction);
        } else {
            final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
            if (patternString != null) {
                mChosenPattern = LockPatternUtils.stringToPattern(patternString);
            }
            updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
        }

        tv_jump.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (changePWD != null) {
                    finish();
                } else if (forgetPWD) {
                    finish();
                } else {
                    startActivity(intent);
                    finish();
                }
            }
        });

        listCache = new ArrayList<CellEntity>();

        listCache = (List<CellEntity>) uitl.getAsObject("cellList");
        if (enterType.equals("isModify") && listCache == null) {
            tv_jump.setText("");
        }
        if (enterType.equals("isRegister")) {//注册
            mHeaderText.setText(R.string.lockpattern_recording_intro_header);
        } else if (enterType.equals("isModify")) {//忘记重新设置
            mHeaderText.setText(R.string.lockpattern_recording_modify_header);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); //统计时长
    }

    private void initPreviewViews() {
        mPreviewViews = new View[3][3];
        mPreviewViews[0][0] = findViewById(R.id.gesturepwd_setting_preview_0);
        mPreviewViews[0][1] = findViewById(R.id.gesturepwd_setting_preview_1);
        mPreviewViews[0][2] = findViewById(R.id.gesturepwd_setting_preview_2);
        mPreviewViews[1][0] = findViewById(R.id.gesturepwd_setting_preview_3);
        mPreviewViews[1][1] = findViewById(R.id.gesturepwd_setting_preview_4);
        mPreviewViews[1][2] = findViewById(R.id.gesturepwd_setting_preview_5);
        mPreviewViews[2][0] = findViewById(R.id.gesturepwd_setting_preview_6);
        mPreviewViews[2][1] = findViewById(R.id.gesturepwd_setting_preview_7);
        mPreviewViews[2][2] = findViewById(R.id.gesturepwd_setting_preview_8);
    }

    private void updatePreviewViews() {
        if (mChosenPattern == null) {
            return;
        }
        Log.i("way", "result = " + mChosenPattern.toString());
        for (LockPatternView.Cell cell : mChosenPattern) {
            Log.i("way", "cell.getRow() = " + cell.getRow() + ", cell.getColumn() = " + cell.getColumn());
            mPreviewViews[cell.getRow()][cell.getColumn()].setBackgroundResource(R.drawable.gesture_create_grid_selected);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
        if (mChosenPattern != null) {
            outState.putString(KEY_PATTERN_CHOICE, LockPatternUtils.patternToString(mChosenPattern));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mUiStage == Stage.HelpScreen) {
                updateStage(Stage.Introduction);
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && mUiStage == Stage.Introduction) {
            updateStage(Stage.HelpScreen);
            return true;
        }
        return false;
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
            patternInProgress();
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        @SuppressWarnings("unchecked")
        public void onPatternDetected(List<LockPatternView.Cell> pattern) {

            if (pattern == null)
                return;
            if (mUiStage == Stage.NeedToConfirm || mUiStage == Stage.ConfirmWrong) {
                if (mChosenPattern == null)
                    throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                if (mChosenPattern.equals(pattern)) {
                    //lock_reset_tv.setVisibility(View.VISIBLE);
                    if (changePWD != null) {
                        if (changePWD.equals("changedlock")) {
                            Type = "Set_Act";
                        } else {
                            Type = "HomeTActivity";
                        }

                        Type = "HomeTActivity";
                    }
                    updateStage(Stage.ChoiceConfirmed);
                } else {
                    updateStage(Stage.ConfirmWrong);
                }
            } else if (mUiStage == Stage.Introduction || mUiStage == Stage.ChoiceTooShort) {
                if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
                    updateStage(Stage.ChoiceTooShort);
                } else {
                    mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
                    updateStage(Stage.FirstChoiceValid);
                    List<CellEntity> list = new ArrayList<CellEntity>();
                    if (pattern != null) {
                        for (LockPatternView.Cell cell : mChosenPattern) {
                            CellEntity entity = new CellEntity();
                            entity.setX(cell.getRow());
                            entity.setY(cell.getColumn());
                            list.add(entity);
                        }
                    }
                    uitl.put("cellList", (Serializable) list);
                }
            } else {
                throw new IllegalStateException("Unexpected stage " + mUiStage + " when " + "entering the pattern.");
            }
            if (forgetPWD) {
                // 忘记密码
                if (mUiStage == Stage.NeedToConfirm || mUiStage == Stage.ConfirmWrong) {
                    if (mChosenPattern == null)
                        throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                    if (mChosenPattern.equals(pattern)) {
                        //lock_reset_tv.setVisibility(View.VISIBLE);
                        Type = "Set_Act";
                        updateStage(Stage.ChoiceConfirmed);
                    } else {
                        updateStage(Stage.ConfirmWrong);
                    }
                } else if (mUiStage == Stage.Introduction || mUiStage == Stage.ChoiceTooShort) {
                    if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
                        updateStage(Stage.ChoiceTooShort);
                    } else {
                        mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
                        updateStage(Stage.FirstChoiceValid);
                        List<CellEntity> list = new ArrayList<CellEntity>();
                        if (pattern != null) {
                            for (LockPatternView.Cell cell : mChosenPattern) {
                                CellEntity entity = new CellEntity();
                                entity.setX(cell.getRow());
                                entity.setY(cell.getColumn());
                                list.add(entity);
                            }
                        }
                        uitl.put("cellList", (Serializable) list);

                    }
                } else {
                    throw new IllegalStateException("Unexpected stage " + mUiStage + " when " + "entering the pattern.");
                }

            }
            // if(changePWD.equals("changedlock")){
            // // 再次创建密码
            // sp.edit().putBoolean("isFirst", false).commit();
            // if (mUiStage == Stage.NeedToConfirm
            // || mUiStage == Stage.ConfirmWrong) {
            // if (mChosenPattern == null){
            // throw new
            // IllegalStateException("null chosen pattern in stage 'need to confirm");
            // }
            // if (mChosenPattern.equals(pattern)) {
            // lock_reset_tv.setVisibility(View.VISIBLE);
            // Type="Set_Act";
            // updateStage(Stage.ChoiceConfirmed);
            //
            // } else {
            // updateStage(Stage.ConfirmWrong);
            // }
            // } else if (mUiStage == Stage.Introduction
            // || mUiStage == Stage.ChoiceTooShort) {
            // if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
            // updateStage(Stage.ChoiceTooShort);
            // } else {
            // mChosenPattern = new ArrayList<LockPatternView.Cell>(
            // pattern);
            // updateStage(Stage.FirstChoiceValid);
            // List<CellEntity> list=new ArrayList<CellEntity>();
            // if(pattern!=null){
            // for(LockPatternView.Cell cell:mChosenPattern){
            // CellEntity entity=new CellEntity();
            // entity.setX(cell.getRow());
            // entity.setY(cell.getColumn());
            // list.add(entity);
            // }
            // }
            // DoCacheUtil
            // uitl=DoCacheUtil.get(CreateGesturePasswordActivity.this);
            // uitl.put("key",(Serializable)list);
            //
            //
            // }
            // } else {
            // throw new IllegalStateException("Unexpected stage "
            // + mUiStage + " when " + "entering the pattern.");
            // }
            //
            //
            // }
            // else{// 以后每次登陆
            // List<LockPatternView.Cell> newPattern=null;
            // List<CellEntity> list=(List<CellEntity>)
            // DoCacheUtil.get(CreateGesturePasswordActivity.this).getAsObject("key");
            // if(list!=null){
            // newPattern=new ArrayList<LockPatternView.Cell>();
            // for(CellEntity entity:list){
            // LockPatternView.Cell
            // cell=LockPatternView.Cell.of(entity.getX(),entity.getY());
            // newPattern.add(cell);
            // }
            // }
            //
            // if (mUiStage == Stage.Introduction
            // || mUiStage == Stage.ChoiceTooShort) {
            // if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
            // updateStage(Stage.ChoiceTooShort);
            // } else {
            // mChosenPattern = new ArrayList<LockPatternView.Cell>(
            // pattern);
            // if(isEquals(newPattern, mChosenPattern)){
            // updateStage(Stage.ChoiceConfirmed);
            //
            // }else{
            // updateStage(Stage.Introduction);
            // }
            //
            // }
            // } else {
            // throw new IllegalStateException("Unexpected stage "
            // + mUiStage + " when " + "entering the pattern.");
            // }
            // }

        }

        public void onPatternCellAdded(List<Cell> pattern) {

        }

        private void patternInProgress() {
            mHeaderText.setText(R.string.lockpattern_recording_inprogress);
            tv_dot.setVisibility(View.INVISIBLE);
            // mFooterLeftButton.setEnabled(false);
            // mFooterRightButton.setEnabled(false);
        }
    };
    private boolean forgetPWD;
    private String changePWD;
    private String enterType;

    private void updateStage(Stage stage) {
        mUiStage = stage;
        if (stage == Stage.ChoiceTooShort) {
            //mHeaderText.setText(getResources().getString(stage.headerMessage,
            //LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
            if (enterType.equals("isRegister")) {//注册
                mHeaderText.setText(R.string.lockpattern_recording_intro_header);
            } else if (enterType.equals("isModify")) {//忘记重新设置
                mHeaderText.setText(R.string.lockpattern_recording_modify_header);
            }
            tv_dot.setText(R.string.lockpattern_recording_incorrect_too_short);
            tv_dot.setVisibility(View.VISIBLE);
            tv_dot.setTextColor(Color.RED);
            tv_dot.startAnimation(mShakeAnim);
        } else if (stage == Stage.ConfirmWrong) {
            mHeaderText.setText(R.string.lockpattern_need_to_confirm);
            tv_dot.setText(R.string.gesture_password_guide_no);
            tv_dot.setVisibility(View.VISIBLE);
            tv_dot.setTextColor(getResources().getColor(R.color.text_d40f42));
            tv_dot.startAnimation(mShakeAnim);
        } else {
            mHeaderText.setText(stage.headerMessage);
            tv_dot.setVisibility(View.INVISIBLE);
        }

        if (stage.patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }

        mLockPatternView.setDisplayMode(DisplayMode.Correct);
        switch (mUiStage) {
            case HelpScreen:
                mLockPatternView.setPattern(DisplayMode.Animate, mAnimatePattern);
                break;
            case ChoiceTooShort:
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            //case FirstChoiceValid:
            //lock_reset_tv.setVisibility(View.VISIBLE);
            //break;
            case NeedToConfirm:
                tv_jump.setVisibility(View.VISIBLE);
                mLockPatternView.clearPattern();
                updatePreviewViews();
                tv_jump.setText("重新绘制");
                tv_jump.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onEvent(CreateGesturePasswordActivity.this, "314002_szssmm_cxsz_click");
                        for (LockPatternView.Cell cell : mChosenPattern) {
                            mPreviewViews[cell.getRow()][cell.getColumn()].setBackgroundResource(R.color.trans);
                        }
                        mLockPatternView.clearPattern();
                        updateStage(Stage.Introduction);
                        if (enterType.equals("isModify") && listCache == null) {
                            tv_jump.setText("");
                        }
                        if (enterType.equals("isRegister")) {//注册
                            mHeaderText.setText(R.string.lockpattern_recording_intro_header);
                        } else if (enterType.equals("isModify")) {//忘记重新设置
                            mHeaderText.setText(R.string.lockpattern_recording_modify_header);
                        }
                    }
                });
                break;
            case ConfirmWrong:
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case ChoiceConfirmed:
                break;
        }

        if (mUiStage.rightMode == RightButtonMode.Continue) {
            if (mUiStage != Stage.FirstChoiceValid) {
                throw new IllegalStateException("expected ui stage " + Stage.FirstChoiceValid + " when button is " + RightButtonMode.Continue);
            }
            updateStage(Stage.NeedToConfirm);
            //lock_reset_tv.setText("取消");
            //lock_reset_tv.setOnClickListener(new OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        if (enterType.equals("isRegister")) {
            //            uitl.put("cellList", "");
            //            startActivity(new Intent(CreateGesturePasswordActivity.this, MainActivity.class));
            //            finish();
            //        } else if (enterType.equals("isModify")) {
            //            // String token =
            //            // LoginUserProvider.getUser(CreateGesturePasswordActivity.this).getToken().toString();
            //            // cancelHandPas(token, "3");
            //            uitl.put("cellList", "");
            //            finish();
            //        }
            //    }
            //});
        } else if (mUiStage.rightMode == RightButtonMode.Confirm) {
            if (mUiStage != Stage.ChoiceConfirmed) {
                throw new IllegalStateException("expected ui stage "
                        + Stage.ChoiceConfirmed + " when button is "
                        + RightButtonMode.Confirm);
            }
            // 保存。。
            // lock_reset_tv.setText("完成");
            // lock_reset_tv.setOnClickListener(new OnClickListener() {
            // @Override
            // public void onClick(View v) {
            // saveChosenPatternAndFinish();
            //
            // }
            // });

            saveChosenPatternAndFinish();
        } else if (mUiStage.rightMode == RightButtonMode.Ok) {
            if (mUiStage != Stage.HelpScreen) {
                throw new IllegalStateException(
                        "Help screen is only mode with ok button, but "
                                + "stage is " + mUiStage);
            }
            mLockPatternView.clearPattern();
            mLockPatternView.setDisplayMode(DisplayMode.Correct);
            updateStage(Stage.Introduction);
        }

    }

    private void saveChosenPatternAndFinish() {
        MyApplication.getInstance().getLockPatternUtils().saveLockPattern(mChosenPattern);
        // DialogUtils.showDialog(CreateGesturePasswordActivity.this,"手势密码设置成功");
        // Login(user_id, password_MD5, phone);
        List<CellEntity> list = (List<CellEntity>) uitl.getAsObject("cellList");

        StringBuffer sb = new StringBuffer();

        for (CellEntity entity : list) {
            for (int i = 0; i < data.length; i++) {
                if (data[i].getX() == entity.getX() && data[i].getY() == entity.getY()) {
                    sb.append(String.valueOf(i));
                }
            }
        }
        String gestruStr = sb.toString();
        String token = LoginUserProvider.getUser(CreateGesturePasswordActivity.this).getToken().toString();
        setHandPas(token, gestruStr, "3");

    }

    // clear the wrong pattern unless they have started a new one
    // already
    private void postClearPatternRunnable() {
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
    }


    public boolean isEquals(List<LockPatternView.Cell> newPatter, List<LockPatternView.Cell> oldPatter) {
        if (oldPatter == null || newPatter == null) {
            return false;
        }
        if (oldPatter.size() != newPatter.size()) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < oldPatter.size(); i++) {
            LockPatternView.Cell oldCell = oldPatter.get(i);
            LockPatternView.Cell newCell = newPatter.get(i);
            if (oldCell.row == newCell.row && oldCell.column == newCell.column) {
                count++;
            }
        }

        if (count == oldPatter.size()) {
            return true;
        } else {
            return false;
        }

    }

    // 设置手势密码
    private void setHandPas(final String token, String gesturePwd, String from) {

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(CreateGesturePasswordActivity.this));
        String url = Urls.SETGESTUREPWD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("gesturePwd", gesturePwd);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("data")) {
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                    }
                    String msg = jsonObject.getString("message");
                    if (jsonObject.getString("state").equals("0")) {
                        getUserInfo(token, "3");
                        Toast.makeText(CreateGesturePasswordActivity.this, msg, 0).show();
                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        finish();
                        String mGesture = LoginUserProvider.getUser(CreateGesturePasswordActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(CreateGesturePasswordActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(CreateGesturePasswordActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        // LoginUserProvider.cleanData(CreateGesturePasswordActivity.this);
                        // LoginUserProvider.cleanDetailData(CreateGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil
                                .get(CreateGesturePasswordActivity.this);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(CreateGesturePasswordActivity.this, msg, 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateGesturePasswordActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(CreateGesturePasswordActivity.this, "请检查网络", 0).show();
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    // 取消手势密码
    private void cancelHandPas(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(CreateGesturePasswordActivity.this));
        String url = Urls.CANCELGESTUREPWD;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        Toast.makeText(CreateGesturePasswordActivity.this,
                                jsonObject.getString("message"), 0).show();
                        uitl.put("cellList", "");
                        getUserInfo(token, "3");
                        // finish();
                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        String mGesture = LoginUserProvider.getUser(
                                CreateGesturePasswordActivity.this)
                                .getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("")
                                && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(
                                    CreateGesturePasswordActivity.this,
                                    UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(
                                    CreateGesturePasswordActivity.this,
                                    LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        // LoginUserProvider.cleanData(CreateGesturePasswordActivity.this);
                        // LoginUserProvider.cleanDetailData(CreateGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil
                                .get(CreateGesturePasswordActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(CreateGesturePasswordActivity.this,
                                jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateGesturePasswordActivity.this, "解析异常",
                            0).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                Toast.makeText(CreateGesturePasswordActivity.this, "请检查网络", 0)
                        .show();
            }
        });
    }

    // 获取用户信息
    private void getUserInfo(final String token, String from) {
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(CreateGesturePasswordActivity.this));
        String url = Urls.GETUSERINFO;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserInfo info = new UserInfo();
                        info.setPhone(dataObj.getString("name"));
                        info.setToken(token);
                        info.setGesturePwd("1");// 是否设置过手势密码，0设置1已设置
                        info.setBankPas(dataObj.getString("businessPwd"));// 交易密码
                        info.setEmail(dataObj.getString("email"));// 邮箱
                        String cgbBindBankCardState = dataObj.getString("cgbBindBankCardState");
                        if(cgbBindBankCardState.equals("null")){
                            info.setIsBindBank("1");//是否绑定银行卡 1未绑定 2已绑定
                        }else{
                            info.setIsBindBank(cgbBindBankCardState);
                        }
                        String certificateChecked = dataObj.getString("certificateChecked");
                        if(certificateChecked.equals("null")){
                            info.setCertificateChecked("1");
                        }else{
                            info.setCertificateChecked(dataObj.getString("certificateChecked"));//是否注册 1未开户  2已开户
                        }
                        info.setAddress(dataObj.getString("address"));// 地址
                        info.setEmergencyUser(dataObj.getString("emergencyUser"));// 紧急联系人
                        info.setEmergencyTel(dataObj.getString("emergencyTel"));// 紧急联系电话
                        info.setRealName(dataObj.getString("realName"));// 真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));// 身份证号
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));// 银行卡号
                        info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        DoCacheUtil util = DoCacheUtil.get(CreateGesturePasswordActivity.this);
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(CreateGesturePasswordActivity.this);
                        util.put("cellList", "cellList");
                        if (!TextUtils.isEmpty(enterType)) {
                            if (enterType.equals("isRegister")) {
                                if (overtime.equals("0")) {// 超时或者未登录状态
                                    Intent bankIntent = new Intent(CreateGesturePasswordActivity.this, BindBankCardActivity.class);
                                    bankIntent.putExtra("isBackAccount", "3");
                                    startActivity(bankIntent);
                                    LoginActivity.getInstance().finish();
                                    RegisterActivity.instance.finish();
                                    finish();
                                } else {
                                    startActivity(intent);
                                    finish();
                                }
                            } else if (enterType.equals("isModify")) {
                                finish();
                            }
                        } else {
                            finish();
                        }
                    } else if (jsonObject.getString("state").equals("4")) {// 系统超时
                        String mGesture = LoginUserProvider.getUser(CreateGesturePasswordActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            // 设置手势密码
                            Intent intent = new Intent(CreateGesturePasswordActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            // 未设置手势密码
                            Intent intent = new Intent(CreateGesturePasswordActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        // LoginUserProvider.cleanData(CreateGesturePasswordActivity.this);
                        // LoginUserProvider.cleanDetailData(CreateGesturePasswordActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(CreateGesturePasswordActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(CreateGesturePasswordActivity.this,
                                jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateGesturePasswordActivity.this, "解析异常",
                            0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                Toast.makeText(CreateGesturePasswordActivity.this, "请检查网络", 0)
                        .show();
            }
        });
    }

}
