package com.ztmg.cicmorgan.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.activity.RequestFriendsActivity;
import com.ztmg.cicmorgan.home.activity.HomeDataH5Activity;
import com.ztmg.cicmorgan.home.entity.HomeProjectEntity;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.activity.SafeInvestmentDetailActivity;
import com.ztmg.cicmorgan.login.LoginActivity;
import com.ztmg.cicmorgan.more.activity.AboutWeActivity;
import com.ztmg.cicmorgan.more.activity.ActivitysActivity;
import com.ztmg.cicmorgan.more.activity.NewsActivity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.DoCacheUtil;
import com.ztmg.cicmorgan.util.LoginUserProvider;
import com.ztmg.cicmorgan.util.TimeUtil;

import java.text.DecimalFormat;
import java.util.List;

public class HomeProjectAdapter extends BaseAdapter {

    private Context mContext;
    private List<HomeProjectEntity> projectList;

    public HomeProjectAdapter(Context context, List<HomeProjectEntity> list) {
        this.mContext = context;
        this.projectList = list;
    }

    @Override
    public int getCount() {
        return projectList.size();
    }

    @Override
    public Object getItem(int position) {
        return projectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.fragment_item_home_project, null);
            holder = new ViewHolder();
            holder.tv_new_text = (TextView) convertView.findViewById(R.id.tv_new_text);
            holder.tv_new_rate = (TextView) convertView.findViewById(R.id.tv_new_rate);
            holder.tv_new_span = (TextView) convertView.findViewById(R.id.tv_new_span);
            holder.bt_enjoy = (TextView) convertView.findViewById(R.id.bt_enjoy);
            holder.bt_gray_enjoy = (TextView) convertView.findViewById(R.id.bt_gray_enjoy);
            holder.iv_right_type = (ImageView) convertView.findViewById(R.id.iv_right_type);
            holder.ll_title = (LinearLayout) convertView.findViewById(R.id.ll_title);
            holder.ll_notice = (LinearLayout) convertView.findViewById(R.id.ll_notice);
            holder.ll_bottom = (LinearLayout) convertView.findViewById(R.id.ll_bottom);
            holder.ll_safe = (LinearLayout) convertView.findViewById(R.id.ll_safe);
            holder.ll_about_we = (LinearLayout) convertView.findViewById(R.id.ll_about_we);
            holder.ll_operation_report = (LinearLayout) convertView.findViewById(R.id.ll_operation_report);
            holder.ll_request_friends = (LinearLayout) convertView.findViewById(R.id.ll_request_friends);
            holder.ll_enterprise_honor = (LinearLayout) convertView.findViewById(R.id.ll_enterprise_honor);//企业荣誉
            holder.ll_information_disclosure = (LinearLayout) convertView.findViewById(R.id.ll_information_disclosure);//信息披露
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
            holder.rl_list = (RelativeLayout) convertView.findViewById(R.id.rl_list);
            holder.iv_risk_education = (ImageView) convertView.findViewById(R.id.iv_risk_education);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (projectList.size() > 0) {
            if (projectList.size() == 1) {
                holder.ll_notice.setVisibility(View.VISIBLE);
                holder.ll_title.setVisibility(View.VISIBLE);
                holder.ll_bottom.setVisibility(View.VISIBLE);
            } else {
                if (position == 0) {
                    holder.ll_notice.setVisibility(View.VISIBLE);
                    holder.ll_title.setVisibility(View.VISIBLE);
                    holder.ll_bottom.setVisibility(View.GONE);
                } else if (position == projectList.size() - 1) {
                    holder.ll_notice.setVisibility(View.GONE);
                    holder.ll_title.setVisibility(View.GONE);
                    holder.ll_bottom.setVisibility(View.VISIBLE);
                } else {
                    holder.ll_notice.setVisibility(View.GONE);
                    holder.ll_title.setVisibility(View.GONE);
                    holder.ll_bottom.setVisibility(View.GONE);
                }
            }
        } else {

            holder.ll_notice.setVisibility(View.VISIBLE);
            holder.ll_title.setVisibility(View.VISIBLE);
            holder.ll_bottom.setVisibility(View.VISIBLE);
        }

        //		if(position==0){
        //			holder.ll_title.setVisibility(View.VISIBLE);
        //			holder.ll_bottom.setVisibility(View.GONE);
        //		}else if(position==projectList.size()-1){
        //			holder.ll_title.setVisibility(View.GONE);
        //			holder.ll_bottom.setVisibility(View.VISIBLE);
        //		}else{
        //			holder.ll_title.setVisibility(View.GONE);
        //			holder.ll_bottom.setVisibility(View.GONE);
        //		}

        final HomeProjectEntity entity = (HomeProjectEntity) getItem(position);
        if (entity.getProjectProductType().equals("1")) {//1安心投2供应链
            holder.tv_new_text.setText(entity.getName());
        } else if (entity.getProjectProductType().equals("2")) {
            holder.tv_new_text.setText(entity.getName() + "(" + entity.getSn() + ")");
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
        String rateDot = decimalFormat.format(Double.parseDouble(entity.getRate()));
        holder.tv_new_rate.setText(rateDot + "%");

        String span = entity.getSpan();
        holder.tv_new_span.setText(span + "天");// 项目期限
        String prostate = entity.getProstate();
        String loandate = entity.getLoandate();
        if (prostate != null && prostate.equals("4")) {
            holder.bt_enjoy.setVisibility(View.VISIBLE);
            holder.bt_gray_enjoy.setVisibility(View.GONE);
            holder.bt_enjoy.setText("立即加入");
            if (loandate != null) {
                boolean isBidders = TimeUtil.compareNowTime(loandate);
                if (isBidders) {
                    holder.bt_enjoy.setVisibility(View.VISIBLE);
                    holder.bt_gray_enjoy.setVisibility(View.GONE);
                    holder.bt_enjoy.setText("立即加入");

                } else {
                    holder.bt_enjoy.setVisibility(View.GONE);
                    holder.bt_gray_enjoy.setVisibility(View.VISIBLE);
                    holder.bt_gray_enjoy.setText("已到期");
                }
            }
        } else if (prostate != null && prostate.equals("3")) {
            holder.bt_enjoy.setVisibility(View.GONE);
            holder.bt_gray_enjoy.setVisibility(View.VISIBLE);
            holder.bt_gray_enjoy.setText("即将上线");
        } else if (prostate != null && prostate.equals("6")) {
            holder.bt_enjoy.setVisibility(View.GONE);
            holder.bt_gray_enjoy.setVisibility(View.VISIBLE);
            holder.bt_gray_enjoy.setText("还款中");
        } else if (prostate != null && prostate.equals("7")) {
            holder.bt_enjoy.setVisibility(View.GONE);
            holder.bt_gray_enjoy.setVisibility(View.VISIBLE);
            holder.bt_gray_enjoy.setText("已还完");
        } else if (prostate != null && prostate.equals("5")) {
            holder.bt_enjoy.setVisibility(View.GONE);
            holder.bt_gray_enjoy.setVisibility(View.VISIBLE);
            holder.bt_gray_enjoy.setText("还款中");
        }
        String type = entity.getProjectType();
        if (type.equals("2")) {//新手2
            holder.iv_right_type.setBackgroundResource(R.drawable.pic_red_new_p_hand);
        } else {//推荐标3
            holder.iv_right_type.setBackgroundResource(R.drawable.pic_red_home_p_discount);
        }
        //企业名字美特好
        if (entity.getProjectProductType().equals("2")) {//供应链
            holder.tv_new_text.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String path = entity.getCreditUrl();
                    if (!TextUtils.isEmpty(path) && !path.equals("null") && path != null) {
                        Intent mthLogoIntent = new Intent(mContext, AgreementActivity.class);
                        mthLogoIntent.putExtra("path", path);
                        mthLogoIntent.putExtra("title", entity.getCreditName());
                        mContext.startActivity(mthLogoIntent);
                    }
                }
            });
        } else if (entity.getProjectProductType().equals("1")) {
            holder.tv_new_text.setClickable(false);
        }

        holder.rl_list.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (entity.getProjectProductType().equals("1")) {//安心投
                    Intent intent = new Intent(mContext, SafeInvestmentDetailActivity.class);
                    intent.putExtra("projectid", entity.getProjectid());
                    intent.putExtra("loanDate", entity.getLoandate());
                    mContext.startActivity(intent);
                }/* else if (entity.getProjectProductType().equals("2")) {//供应链
                    Intent intent = new Intent(mContext, InvestmentDetailActivity.class);
                    intent.putExtra("projectid", entity.getProjectid());
                    intent.putExtra("loanDate", entity.getLoandate());
                    mContext.startActivity(intent);
                }*/
            }
        });

        //信息披露
        holder.ll_safe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent safeNoDataIntent = new Intent(mContext, HomeDataH5Activity.class);
                safeNoDataIntent.putExtra("url", Urls.DISCLOSUREHOME);
                //				safeNoDataIntent.putExtra("url", "http://192.168.1.17:8020/cic-wap/disclosure_home.html");
                safeNoDataIntent.putExtra("name", "信息披露");
                mContext.startActivity(safeNoDataIntent);
            }
        });
        //关于我们
        holder.ll_about_we.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AboutWeActivity.class);
                mContext.startActivity(intent);
            }
        });
        //运营数据
        holder.ll_operation_report.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent operationReportIntent = new Intent(mContext, HomeDataH5Activity.class);
                operationReportIntent.putExtra("url", Urls.MOREDATA);
                operationReportIntent.putExtra("name", "运营数据");
                mContext.startActivity(operationReportIntent);
            }
        });
        //邀请好友
        holder.ll_request_friends.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (LoginUserProvider.getUser(mContext) != null) {
                    DoCacheUtil util = DoCacheUtil.get(mContext);
                    String str = util.getAsString("isLogin");
                    if (str != null) {
                        if (str.equals("isLogin")) {//已登录
                            Intent invitIntent = new Intent(mContext, RequestFriendsActivity.class);
                            mContext.startActivity(invitIntent);
                        } else {//未登录
                            Intent loginIntent = new Intent(mContext, LoginActivity.class);
                            loginIntent.putExtra("overtime", "6");//无论登录界面返回还是登录成功，都是finish当前界面
                            mContext.startActivity(loginIntent);
                        }
                    } else {
                        Intent loginIntent1 = new Intent(mContext, LoginActivity.class);
                        loginIntent1.putExtra("overtime", "6");
                        mContext.startActivity(loginIntent1);
                    }
                } else {
                    Intent loginIntent2 = new Intent(mContext, LoginActivity.class);
                    loginIntent2.putExtra("overtime", "6");
                    mContext.startActivity(loginIntent2);
                }
            }
        });
        //出借人风险教育
        holder.iv_risk_education.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent riskEducationIntent = new Intent(mContext, HomeDataH5Activity.class);
                riskEducationIntent.putExtra("url", Urls.MOREEDUCATION);
                riskEducationIntent.putExtra("name", "风险教育");
                mContext.startActivity(riskEducationIntent);
            }
        });
        //企业荣誉
        holder.ll_enterprise_honor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewsActivity.class);
                mContext.startActivity(intent);
            }
        });
        //信息披露
        holder.ll_information_disclosure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivitysActivity.class);
                mContext.startActivity(intent);
            }
        });

        holder.tv_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent riskTipFirstIntent = new Intent(mContext, AgreementActivity.class);
                riskTipFirstIntent.putExtra("path", Urls.ZTPROTOCOLRISK);
                riskTipFirstIntent.putExtra("title", "风险提示书");
                mContext.startActivity(riskTipFirstIntent);
            }
        });

        return convertView;
    }

    public class ViewHolder {
        private TextView tv_new_text, tv_new_rate, tv_new_span;
        private TextView bt_enjoy, bt_gray_enjoy;
        private ImageView iv_right_type;
        private LinearLayout ll_notice, ll_title, ll_bottom;
        private LinearLayout ll_safe, ll_operation_report, ll_about_we, ll_request_friends;
        private LinearLayout ll_enterprise_honor, ll_information_disclosure;
        private TextView tv_text;
        private RelativeLayout rl_list;
        private ImageView iv_risk_education;

    }
}
