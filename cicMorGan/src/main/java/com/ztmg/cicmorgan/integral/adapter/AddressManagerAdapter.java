package com.ztmg.cicmorgan.integral.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.activity.UnlockGesturePasswordActivity;
import com.ztmg.cicmorgan.integral.activity.BuildReceiptAddressActivity;
import com.ztmg.cicmorgan.integral.entity.AddressManagerEntity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.StringUtils;
import com.ztmg.cicmorgan.view.CustomProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.umeng.analytics.MobclickAgent.onEvent;

/**
 * 地址管理
 */
public class AddressManagerAdapter extends BaseAdapter {

    private Context mContext;
    private List<AddressManagerEntity> addressList;
    OnUpdateAddring mOnUpdateAddring;
    private AddressManagerEntity entity;
    private int index;
    private String isDefault;

    public OnUpdateAddring getmOnUpdateAddring() {
        return mOnUpdateAddring;
    }

    public void setmOnUpdateAddring(OnUpdateAddring mOnUpdateAddring) {
        this.mOnUpdateAddring = mOnUpdateAddring;
    }

    public AddressManagerAdapter(Context context, List<AddressManagerEntity> list) {
        this.mContext = context;
        this.addressList = list;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.activity_item_address_manager, null);
            holder.tv_addressee_name = (TextView) convertView.findViewById(R.id.tv_addressee_name);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_shipping_address_name = (TextView) convertView.findViewById(R.id.tv_shipping_address_name);
            holder.mRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.mRelativeLayout);
            holder.tv_edit = (TextView) convertView.findViewById(R.id.tv_edit);
            //holder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
            //holder.ll_default = (LinearLayout) convertView.findViewById(R.id.ll_default);
            //holder.ll_modify = (LinearLayout) convertView.findViewById(R.id.ll_modify);
            //holder.ll_delete = (LinearLayout) convertView.findViewById(R.id.ll_delete);
            //holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //        if (position == addressList.size() - 1) {
        //            holder.tv_tips.setVisibility(View.VISIBLE);
        //        } else {
        //            holder.tv_tips.setVisibility(View.GONE);
        //        }
        //        holder.tv_tips.setOnClickListener(new OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                Intent riskTipFirstIntent = new Intent(mContext, AgreementActivity.class);
        //                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
        //                riskTipFirstIntent.putExtra("title", "风险提示书");
        //                mContext.startActivity(riskTipFirstIntent);
        //            }
        //        });
        entity = (AddressManagerEntity) getItem(position);
        holder.tv_addressee_name.setText(entity.getName());
        holder.tv_phone.setText(StringUtils.phoneEncrypt(entity.getMobile()));

        //是否设置为默认地址 0-否 1-是）
        isDefault = entity.getIsDefault();
        if (isDefault.equals("否")) {
            //不是
            //holder.iv_check.setBackgroundResource(R.drawable.ic_chose);
            holder.tv_shipping_address_name.setText(entity.getProvince() + entity.getCity() + entity.getAddress());
        } else if (isDefault.equals("是")) {
            //holder.iv_check.setBackgroundResource(R.drawable.pic_check_true);
            String s = entity.getProvince() + entity.getCity() + entity.getAddress();
            String str = "<font color='#cbb693'>[默认]</font>" + s;
            holder.tv_shipping_address_name.setText(Html.fromHtml(str));
        }
        //        if (isDefault.equals("是")) {
        //            //holder.iv_check.setClickable(false);
        //        } else {
        //            //默认地址
        //            holder.ll_default.setOnClickListener(new View.OnClickListener() {
        //
        //                @Override
        //                public void onClick(View v) {
        //                    index = position;
        //                    setDefaultAddr("3", LoginUserProvider.getUser(mContext).getToken(), addressList.get(position).getId(), index);
        //                }
        //            });
        //        }
        //编辑
        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEvent(mContext, "701003_glshdz_bjshdz_click");
                for (AddressManagerEntity addressManagerEntity : addressList) {
                    String s = addressManagerEntity.getIsDefault();
                    if (s.equals("是")) {
                        isDefault = "是";
                        break;
                    } else if (s.equals("否")) {
                        isDefault = "否";
                    }
                }
                Intent intent = new Intent(mContext, BuildReceiptAddressActivity.class);
                intent.putExtra("isDefault", isDefault);
                intent.putExtra("newifmodify", "modify");
                intent.putExtra("id", addressList.get(position).getId());
                mContext.startActivity(intent);
            }
        });
        //        //删除
        //        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //            DeleteDialog    dialog = new DeleteDialog(mContext, R.style.SelectPicDialog, position);
        //                dialog.show();
        //            }
        //        });
        return convertView;
    }

    public class ViewHolder {
        private TextView tv_addressee_name, tv_phone, tv_shipping_address_name, tv_edit;
        private RelativeLayout mRelativeLayout;
        // private ImageView iv_check;
        // private LinearLayout ll_default, ll_modify, ll_delete;
    }


    //修改默认地址
    private void setDefaultAddr(String from, String token, String id, final int index) {
        CustomProgress.show(mContext);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        //		AsyncHttpClient client = new AsyncHttpClient();
        //		client.setSSLSocketFactory(SSLSocketFactory.getSocketFactory(mContext));
        String url = Urls.SETONEADDRESSDEFAULT;
        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("token", token);
        params.put("id", id);
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CustomProgress.CustomDismis();
                String result = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("state").equals("0")) {
                        mOnUpdateAddring.onRefreshDefault(index);
                        Toast.makeText(mContext, "设置成功", 0).show();

                    } else if (jsonObject.getString("state").equals("4")) {//系统超时
                        String mGesture = LoginUserProvider.getUser(mContext).getGesturePwd();
                        if (mGesture.equals("1") && !mGesture.equals("") && mGesture != null) {// 判断是否设置手势密码
                            //设置手势密码
                            Intent intent = new Intent(mContext, UnlockGesturePasswordActivity.class);
                            intent.putExtra("overtime", "0");
                            mContext.startActivity(intent);
                        } else {
                            //未设置手势密码
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.putExtra("overtime", "0");
                            mContext.startActivity(intent);
                        }
                        //						LoginUserProvider.cleanData(getActivity());
                        //						LoginUserProvider.cleanDetailData(getActivity());
                        DoCacheUtil util = DoCacheUtil.get(mContext);
                        util.put("isLogin", "");
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("message"), 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "解析异常", 0).show();
                }
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody,
                                  Throwable error) {
                CustomProgress.CustomDismis();
                Toast.makeText(mContext, "请检查网络", 0).show();
            }
        });
    }


    public interface OnUpdateAddring {
        void onDeleteItem(int positon);

        void onRefreshDefault(int positon);
    }
}
