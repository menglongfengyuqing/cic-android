package com.ztmg.cicmorgan.account.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.AccountMessageActivity.SelectPicDialog;
import com.ztmg.cicmorgan.account.picture.BitmapUtils;
import com.ztmg.cicmorgan.account.picture.ImgEntity;
import com.ztmg.cicmorgan.account.picture.PicGridActivity;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.base.BaseActivity;
import com.ztmg.cicmorgan.entity.UserInfo;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.ImageLoaderUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.SSLSocketFactory;
import com.ztmg.cicmorgan.util.SystemBarTintManager;
import com.ztmg.cicmorgan.util.ToastUtils;
import com.ztmg.cicmorgan.view.CustomProgress;

public class UploadHeadImgActivity extends BaseActivity implements OnClickListener {
    private ImageView iv_upload_header;
    private Button bt_upload_header_img;
    private static final int TAKE_PHOTO_WITH_DATA = 105;//拍照
    private static final int TAKE_PIC = 101;
    private String picPath;
    private String path = Environment.getExternalStorageDirectory().getPath();
    File file;
    Uri imgUri;
    private List<ImgEntity> tempList;//临时带图片用
    private List<ImgEntity> list;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private String herderUrl;
    private static String resultMessage;//返回信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_upload_headimg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(UploadHeadImgActivity.this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.text_cccccc);//通知栏所需颜色
        }
        mInflater = LayoutInflater.from(UploadHeadImgActivity.this);
        mImageLoader = ImageLoaderUtil.getImageLoader();
        mDisplayImageOptions = ImageLoaderUtil.getDisplayImageOptions(R.drawable.pic_defaultheadimage, false, false, false);
        Intent intent = getIntent();
        herderUrl = intent.getStringExtra("herderUrl");
        initView();
        initData();

    }

    @Override
    protected void initView() {
        setTitle("个人头像");
        setBack(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_upload_header = (ImageView) findViewById(R.id.iv_upload_header);
        iv_upload_header.setOnClickListener(this);
        if (!TextUtils.isEmpty(herderUrl)) {
            mImageLoader.displayImage(herderUrl, iv_upload_header, mDisplayImageOptions);
        }
        bt_upload_header_img = (Button) findViewById(R.id.bt_upload_header_img);
        bt_upload_header_img.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tempList = new ArrayList<ImgEntity>();
        list = new ArrayList<ImgEntity>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_upload_header_img:
                //			if(list.size()>0){
                //				//上传更改头像
                //				CustomProgress.show(UploadHeadImgActivity.this);
                //				new Thread(networkTask).start();
                //				//				if(!TextUtils.isEmpty(herderUrl)){//上传更改头像
                //				//					new Thread(networkTask).start();
                //				//				}
                //			}
                SelectPicDialog selectDialog = new SelectPicDialog(UploadHeadImgActivity.this, R.style.SelectPicDialog);
                Window dialogWindow = selectDialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = LayoutParams.FILL_PARENT;
                dialogWindow.setAttributes(lp);
                selectDialog.show();
                break;

            default:
                break;
        }
    }

    //拍照对话框
    public class SelectPicDialog extends Dialog {
        Context context;

        public SelectPicDialog(Context context) {
            super(context);
            this.context = context;
        }

        public SelectPicDialog(Context context, int theme) {
            super(context, theme);
            this.context = context;
            this.setContentView(R.layout.dialog_select_pic);
            TextView tv_camera = (TextView) findViewById(R.id.tv_camera);
            TextView tv_photo = (TextView) findViewById(R.id.tv_photo);
            TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
            tv_camera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //相机
                    takePic();
                    dismiss();
                }
            });
            tv_photo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //相册
                    Intent intent = new Intent(UploadHeadImgActivity.this, PicGridActivity.class);
                    //					tempList.clear();
                    //					tempList.addAll(list);
                    if (tempList != null && tempList.size() > 0) {
                        List<ImgEntity> listname = new ArrayList<ImgEntity>();
                        for (ImgEntity en : tempList) {
                            if (en.getName() != null) {
                                if (en.getName().equals("default_pic") || en.getName().equals("example_pic")) {
                                    listname.add(en);
                                }
                            }
                        }
                        tempList.removeAll(listname);
                    }
                    //					if(type.equals("1")){
                    //						for(int j=0;j<list.size();j++){
                    //							ImgEntity en = new ImgEntity();
                    //							en.setUri(list.get(j).getUri());
                    //							tempList.add(en);
                    //						}
                    //					}
                    intent.putExtra("ImgList", (Serializable) tempList);
                    intent.putExtra("ImgSize", 1);
                    startActivityForResult(intent, TAKE_PIC);
                    dismiss();
                }
            });
            tv_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //取消
                    dismiss();
                }
            });

        }
    }

    /**
     * 开启拍照
     */
    private void takePic() {
        long time = System.currentTimeMillis();

        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        String times = mCalendar.get(Calendar.HOUR) + "-" + mCalendar.get(Calendar.MINUTE) + "-" + mCalendar.get(Calendar.SECOND);

        picPath = path + "/" + times + ".jpg";
        file = new File(picPath);
        if (file.exists()) {
            file.delete();
        }
        imgUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, TAKE_PHOTO_WITH_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PIC:
                    list.clear();
                    List<ImgEntity> backImageBeanList = (List<ImgEntity>) data.getSerializableExtra("PIC");
                    ToastUtils.show(UploadHeadImgActivity.this, "返回的集合" + backImageBeanList.size());
                    list.addAll(backImageBeanList);
                    if (list.size() > 0) {
                        //上传更改头像
                        CustomProgress.show(UploadHeadImgActivity.this);
                        new Thread(networkTask).start();

                    }
                    //				if (list.size() < 2) {
                    //					ImgEntity entity1 = new ImgEntity("", "default_pic", "",
                    //							null, false);
                    //					list.add(entity1);
                    //				}
                    //				111
                    //				adapter.notifyDataSetChanged();
                    //				recycle_view.scrollToPosition(list.size() - 1);
                    break;

                //			case TAKE_PHOTO_WITH_DATA://拍照
                //	            cropImageUri(imgUri, 800, 800, PHOTO_PICKED_WITH_DATA);//开始裁剪
                //				break;
                //			case PHOTO_PICKED_WITH_DATA://裁剪后
                case TAKE_PHOTO_WITH_DATA://拍照
                    list.clear();
                    if (imgUri != null) {
                        //	                Bitmap bitmap = decodeUriAsBitmap(imgUri);

                        ImgEntity entity = new ImgEntity();
                        entity.setUri(imgUri.toString());
                        entity.setPath(file.getPath());
                        //	                Bean_Img bean = new Bean_Img(, "haha", 0, 0, null);

                        list.add(entity);
                        if (list.size() > 0) {
                            CustomProgress.show(UploadHeadImgActivity.this);
                            new Thread(networkTask).start();
                        }

                        //					if(list.size()<=3){
                        //						for(int i=0;i<list.size();i++){
                        //							if(!TextUtils.isEmpty(list.get(i).getName())&&list.get(i).getName().contains("default_pic")){
                        //								list.remove(i);
                        //							}
                        //							if(!TextUtils.isEmpty(list.get(i).getName())&&list.get(i).getName().contains("example_pic")){
                        //								list.remove(i);
                        //							}
                        //						}
                        //					}
                        //					if (list.size() < 2) {
                        //
                        //						ImgEntity entity1 = new ImgEntity("", "default_pic", "",
                        //								null, false);
                        //						list.add(entity1);
                        //					}
                        //					adapter.notifyDataSetChanged();
                        //					recycle_view.scrollToPosition(list.size() - 1);
                        //	                adapter.notifyDataSetChanged();
                        //	                img_pz.setImageBitmap(bitmap);

                    }
                    break;

                default:
                    break;
            }
        }
    }

    //上传图片
    public static String post(String url, Map<String, String> params, Map<String, File> files)
            throws IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(10 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(entry.getValue());
            sb.append(LINEND);
        }
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        // 发送文件数据
        if (files != null)

            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getValue().getName() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                outStream.write(LINEND.getBytes());
            }
        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        StringBuilder sb2 = new StringBuilder();
        if (res == 200) {
            int ch;
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        outStream.close();
        conn.disconnect();
        resultMessage = sb2.toString();
        return sb2.toString();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // TODO
            // UI界面的更新等相关操作
            //			CustomProgress.CustomDismis();
            Bundle data = msg.getData();
            String val = data.getString("value");
            try {
                JSONObject object = new JSONObject(val);
                if (object.getString("state").equals("0")) {
                    mImageLoader.displayImage(list.get(0).getUri(), iv_upload_header, mDisplayImageOptions);
                    ToastUtils.show(UploadHeadImgActivity.this, "提交成功");
                    getUserInfo(LoginUserProvider.getUser(UploadHeadImgActivity.this).getToken(), "3");
                } else if (object.getString("state").equals("4")) {//系统超时
                    String mGesture = LoginUserProvider.getUser(UploadHeadImgActivity.this).getGesturePwd();
                    if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                        //设置手势密码
                        Intent intent = new Intent(UploadHeadImgActivity.this, UnlockGesturePasswordActivity.class);
                        intent.putExtra("overtime", "0");
                        startActivity(intent);
                    } else {
                        //未设置手势密码
                        Intent intent = new Intent(UploadHeadImgActivity.this, LoginActivity.class);
                        intent.putExtra("overtime", "0");
                        startActivity(intent);
                    }
                    DoCacheUtil util = DoCacheUtil.get(UploadHeadImgActivity.this);
                    util.put("isLogin", "");
                } else if (object.getString("state").equals("5")) {//重复添加
                    ToastUtils.show(UploadHeadImgActivity.this, "基本信息，请勿重复添加");

                } else if (object.getString("state").equals("2")) {
                    ToastUtils.show(UploadHeadImgActivity.this, "缺少参数");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("mylog", "请求结果为-->" + val);
        }
    };

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            final Map<String, String> params = new HashMap<String, String>();
            if (LoginUserProvider.getUser(UploadHeadImgActivity.this) != null) {
                params.put("token", LoginUserProvider.getUser(UploadHeadImgActivity.this).getToken());
            }
            final Map<String, File> files = new HashMap<String, File>();
            files.put("file_1", saveMyBitmap(list.get(0)));
            try {
                post(Urls.UPLOADAVATAR, params, files);

            } catch (IOException e) {
                //				CustomProgress.CustomDismis();
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(resultMessage)) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", resultMessage);
                msg.setData(data);
                handler.sendMessage(msg);
            }

            CustomProgress.CustomDismis();
        }
    };

    public File saveMyBitmap(ImgEntity beanImg) {

        File file = null;
        //			if(beanImg.getUri().contains("http")){
        //				Bitmap mBitmap= null;
        //				byte[] data;
        //				try {
        //					data = getImage(beanImg.getUri());
        //					if(data!=null){
        //						mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
        //					}
        //				} catch (Exception e) {
        //					e.printStackTrace();
        //				}
        //
        //				file = new File(beanImg.getPath());
        //				if (!file.exists()) {
        //
        //					try {
        //						file.createNewFile();
        //					} catch (IOException e) {
        //						// TODO Auto-generated catch block
        //						e.printStackTrace();
        //					}
        //
        //				}
        //				FileOutputStream fos = null;
        //
        //				try {
        //					fos = new FileOutputStream(file);
        //				} catch (FileNotFoundException e) {
        //					// TODO Auto-generated catch block
        //					e.printStackTrace();
        //				}
        //
        //
        //				if(mBitmap!=null){
        //					mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        //				}
        //
        //				if (null != fos) {
        //
        //					mBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
        //
        //					try {
        //						fos.flush();
        //					} catch (IOException e) {
        //						// TODO Auto-generated catch block
        //						e.printStackTrace();
        //					}
        //
        //					try {
        //						fos.close();
        //					} catch (IOException e) {
        //						// TODO Auto-generated catch block
        //						e.printStackTrace();
        //					}
        //
        //				}


        //			}else{
        Uri uri = Uri.parse(beanImg.getUri());
        Bitmap mpBitmap = null;

        try {

            mpBitmap = BitmapUtils.getBitmap(getContentResolver(), uri);
            file = new File(beanImg.getPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (mpBitmap != null) {
            mpBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //			}

        return file;
    }

    //获取用户信息
    private void getUserInfo(final String token, String from) {
        CustomProgress.show(UploadHeadImgActivity.this);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //				AsyncHttpClient client = new AsyncHttpClient();
        //				client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(UploadHeadImgActivity.this));
        String url = Urls.GETUSERINFO;
        RequestParams params = new RequestParams();
        params.put("token", token);
        params.put("from", from);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        UserInfo info = new UserInfo();
                        info.setToken(token);
                        info.setPhone(dataObj.getString("name"));
                        info.setBankPas(dataObj.getString("businessPwd"));//交易密码
                        info.setEmail(dataObj.getString("email"));//邮箱
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
                        info.setGesturePwd(dataObj.getString("gesturePwd"));//是否设置过手势密码，0设置1已设置
                        info.setAddress(dataObj.getString("address"));//地址
                        info.setEmergencyUser(dataObj.getString("emergencyUser"));//紧急联系人
                        info.setEmergencyTel(dataObj.getString("emergencyTel"));//紧急联系电话
                        info.setRealName(dataObj.getString("realName"));//真实姓名
                        info.setIdCard(dataObj.getString("IdCard"));//身份证号
                        info.setBindBankCardNo(dataObj.getString("bindBankCardNo"));//银行卡号
                        info.setSigned(dataObj.getString("signed"));//是否签到 3：未签到，2：已签到
                        info.setIsTest(dataObj.getString("isTest"));//是否测试
                        info.setUserType(dataObj.getString("userType"));//测试类型
                        info.setAvatarPath(dataObj.getString("avatarPath"));//头像地址
                        LoginUserProvider.setUser(info);
                        LoginUserProvider.saveUserInfo(UploadHeadImgActivity.this);
                        //Intent intent = new Intent(BindBankCardActivity.this,BindBankSuccessActivity.class);//绑卡成功
                        //intent.putExtra("isBackAccount", isBackAccount);
                        //startActivity(intent);
                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(UploadHeadImgActivity.this).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(UploadHeadImgActivity.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(UploadHeadImgActivity.this, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            startActivity(intent);
                        }
                        //LoginUserProvider.cleanData(BindBankCardActivity.this);
                        //LoginUserProvider.cleanDetailData(BindBankCardActivity.this);
                        DoCacheUtil util = DoCacheUtil.get(UploadHeadImgActivity.this);
                        util.put("isLogin", "");

                    } else {
                        Toast.makeText(UploadHeadImgActivity.this, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UploadHeadImgActivity.this, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(UploadHeadImgActivity.this, "请检查网络", 0).show();
            }
        });
    }

}
