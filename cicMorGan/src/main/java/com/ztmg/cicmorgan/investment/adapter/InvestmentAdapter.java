package com.ztmg.cicmorgan.investment.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.investment.activity.AgreementActivity;
import com.ztmg.cicmorgan.investment.entity.SupplyChainInversMentEntity;
import com.ztmg.cicmorgan.net.Urls;
import com.ztmg.cicmorgan.util.TimeUtil;
import com.ztmg.cicmorgan.view.MySeekBar;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 投资列表adapter
 *
 * @author pc
 */
public class InvestmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<SupplyChainInversMentEntity.DataBean.ListZCBean> investmentList;
    private List<String> isRecommendList = new ArrayList<String>();
    private int type;//判断是否是供应链还是安心头//1：安心投类，2：供应链类
    //	private HorizontalProgressBarWithNumber textProgressBar;

    public InvestmentAdapter(Context context, List<SupplyChainInversMentEntity.DataBean.ListZCBean> list, int type) {
        this.mContext = context;
        this.investmentList = list;
        this.type = type;
    }

    @Override
    public int getCount() {
        return investmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return investmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (type == 1) {////1：安心投类，2：供应链类
                convertView = View.inflate(mContext, R.layout.fragment_item_safeinvest, null);
                holder.v_safe_line = convertView.findViewById(R.id.v_safe_line);
            } else {
                convertView = View.inflate(mContext, R.layout.fragment_item_investment, null);

            }
            holder.tv_good_second_project_name = (TextView) convertView.findViewById(R.id.tv_good_second_project_name);//产品名称
            holder.tv_good_second_project_rate_first = (TextView) convertView.findViewById(R.id.tv_good_second_project_rate_first);//预期出借利率 点之前
            holder.baifenhao = (TextView) convertView.findViewById(R.id.baifenhao);
            holder.tv_good_second_project_rate_second = (TextView) convertView.findViewById(R.id.tv_good_second_project_rate_second);//预期出借利率 点之后
            holder.tv_good_second_project_span_chain = (TextView) convertView.findViewById(R.id.tv_good_second_project_span_chain);//项目期限
            holder.tv_day_text = (TextView) convertView.findViewById(R.id.tv_day_text);
            holder.sb_progress = (MySeekBar) convertView.findViewById(R.id.sb_progress);
            holder.txt_hot = (TextView) convertView.findViewById(R.id.txt_hot);
            holder.txt_project_state = (TextView) convertView.findViewById(R.id.txt_project_state);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == investmentList.size() - 1) {
            holder.linearLayout.setVisibility(View.VISIBLE);
        } else {
            holder.linearLayout.setVisibility(View.GONE);
        }
        final SupplyChainInversMentEntity.DataBean.ListZCBean entity = (SupplyChainInversMentEntity.DataBean.ListZCBean) getItem(position);
        if (entity != null) {

            //String isRecommend = entity.getIsRecommend();
            //DecimalFormat decimalFormat4 = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
            //String rateDot4 = decimalFormat4.format(Double.parseDouble(entity.getAnnualRate()));
            //holder.tv_good_second_project_rate_first.setText(rateDot4);

            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
            if (!decimalFormat.format(Double.parseDouble(entity.getInterestRateIncrease())).equals("0.00")) {
                //得到基本利率 =  总利率-加息利率
                Double basic = Double.parseDouble(entity.getAnnualRate()) - Double.parseDouble(entity.getInterestRateIncrease());
                String rateDot = decimalFormat.format(basic);
                holder.tv_good_second_project_rate_first.setText(rateDot);
                String interestRateIncrease = decimalFormat.format(Double.parseDouble(entity.getInterestRateIncrease()));
                holder.tv_good_second_project_rate_second.setText("+" + interestRateIncrease + "%");
                holder.tv_good_second_project_rate_second.setVisibility(View.VISIBLE);
            } else {
                String rateDot = decimalFormat.format(Double.parseDouble(entity.getAnnualRate()));
                holder.tv_good_second_project_rate_first.setText(rateDot);
                holder.tv_good_second_project_rate_second.setVisibility(View.GONE);
            }
            holder.txt_project_state.setText(entity.getState());
            holder.tv_good_second_project_span_chain.setText(entity.getSpan() + "");

            if (entity.isHot()) {
                holder.txt_hot.setVisibility(View.VISIBLE);
            } else {
                holder.txt_hot.setVisibility(View.GONE);
            }

            if (type == 2) {
                holder.tv_good_second_project_name.setText(entity.getName() + '(' + entity.getSn() + ')');
                double currentAmount = entity.getCurrentAmount();
                double amount = entity.getAmount();
                if (entity.getAmount() != 0) {
                    double percentage = ((double) currentAmount / amount) * 100;
                    BigDecimal bd = new BigDecimal(percentage).setScale(0, BigDecimal.ROUND_HALF_UP);
                    int percentageInt = (int) bd.doubleValue();

                    // percentageInt 得到的是百分比，如果比等于100 就显示为100，如果大于99.5小于100显示99，如果小于1，显示1，其他正常
                    if (percentage == 100) {
                        holder.sb_progress.setProgress(100);
                    } else if (percentage > 99.5 && percentage < 100) {
                        holder.sb_progress.setProgress(99);
                    } else if (percentage < 0.5 && percentage > 0) {
                        holder.sb_progress.setProgress(1);
                    } else if (percentage == 0.0) {
                        holder.sb_progress.setProgress(0);
                    } else {
                        holder.sb_progress.setProgress(percentageInt);
                    }
                }
                //int progress= Integer.valueOf(entity.getPercentage().replace("%","").toString().substring(0, entity.getPercentage().replace("%","").toString().indexOf("."))).intValue();
                //holder.sb_progress.setProgress(progress);
                //holder.sb_progress.setProgress(Integer.parseInt(entity.getPercentage()));
                if (entity.getState().equals("4")) {//4是立即投资
                    if (!TextUtils.isEmpty(entity.getLoanDate()) && !entity.getLoanDate().equals("null")) {
                        String loanTime = entity.getLoanDate();
                        boolean isBidders = TimeUtil.compareInverstmentListNowTime(loanTime);
                        if (isBidders) {
                            if (entity.getAmount() != 0) {
                                double percentage = (entity.getCurrentAmount() / entity.getAmount()) * 100;
                                //double percentage = (0 / 50000) * 100;
                                BigDecimal decimal = new BigDecimal(percentage).setScale(0, BigDecimal.ROUND_HALF_UP);
                                int percentageInt1 = (int) decimal.doubleValue();
                                if (percentage == 100) {
                                    holder.sb_progress.setProgress(100);
                                    holder.txt_project_state.setText("已投100%");
                                } else if (percentage > 99.5 && percentage < 100) {
                                    holder.sb_progress.setProgress(99);
                                    holder.txt_project_state.setText("已投99%");
                                } else if (percentage < 0.5 && percentage > 0) {
                                    holder.sb_progress.setProgress(1);
                                    holder.txt_project_state.setText("已投1%");
                                } else if (percentage == 0.0) {
                                    holder.sb_progress.setProgress(0);
                                    holder.txt_project_state.setText("已投0%");
                                } else {
                                    holder.sb_progress.setProgress(percentageInt1);
                                    holder.txt_project_state.setText("已投" + percentageInt1 + "%");
                                }
                            }
                            holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                            holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                            holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                            holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                            holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                            holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                            holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));

                            holder.sb_progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.seekbar));
                            //Drawable drawable = getNewDrawable(mContext, R.drawable.img_circle_red, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            holder.sb_progress.setThumb(mContext.getResources().getDrawable(R.drawable.img_circle_red));
                        } else {
                            holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_989898));

                            holder.sb_progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.seekbar_grey));
                            //Drawable drawablegrey = getNewDrawable(mContext, R.drawable.img_circle_gray,LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            holder.sb_progress.setThumb(mContext.getResources().getDrawable(R.drawable.img_circle_gray));
                            holder.txt_project_state.setText("已到期");
                        }
                    }
                } else if (entity.getState().equals("3")) {//即将上线
                    holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));

                    holder.sb_progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.seekbar));
                    holder.txt_project_state.setText("即将上线");
                } else if (entity.getState().equals("6") || entity.getState().equals("5")) {//还款中
                    holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_989898));

                    holder.sb_progress.setProgress(100);
                    holder.sb_progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.seekbar));
                    Drawable drawable = getNewDrawable(mContext, R.drawable.progess_red, 5, 5);
                    holder.sb_progress.setThumb(drawable);
                    holder.txt_project_state.setText("还款中");
                } else if (entity.getState().equals("7")) {//已还完
                    holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_989898));

                    holder.sb_progress.setProgress(100);
                    holder.sb_progress.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.seekbar_grey));
                    Drawable drawable = getNewDrawable(mContext, R.drawable.progress_grey, 5, 5);
                    holder.sb_progress.setThumb(drawable);
                    holder.txt_project_state.setText("已还款");
                }
            } else {//安心投
                holder.tv_good_second_project_name.setText(entity.getName());
                if (entity.getState().equals("4")) {//4是立即投资
                    if (!TextUtils.isEmpty(entity.getLoanDate()) && !entity.getLoanDate().equals("null")) {
                        String loanTime = entity.getLoanDate();
                        boolean isBidders = TimeUtil.compareInverstmentListNowTime(loanTime);
                        if (isBidders) {

                            holder.txt_project_state.setText("可加入");
                            holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                            holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                            holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                            holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                            holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                            holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                            holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                            holder.v_safe_line.setBackgroundColor(mContext.getResources().getColor(R.color.text_a11c3f));
                        } else {
                            holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                            holder.v_safe_line.setBackgroundColor(mContext.getResources().getColor(R.color.text_989898));

                            holder.txt_project_state.setText("已到期");
                        }
                    }
                } else if (entity.getState().equals("3")) {//即将上线
                    holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.v_safe_line.setBackgroundColor(mContext.getResources().getColor(R.color.text_a11c3f));

                    holder.txt_project_state.setText("即将上线");
                } else if (entity.getState().equals("6") || entity.getState().equals("5")) {//还款中
                    holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_34393c));
                    holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.v_safe_line.setBackgroundColor(mContext.getResources().getColor(R.color.text_989898));

                    holder.txt_project_state.setText("还款中");
                } else if (entity.getState().equals("7")) {//已还完
                    holder.tv_good_second_project_name.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_good_second_project_rate_first.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.baifenhao.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_good_second_project_rate_second.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_good_second_project_span_chain.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.tv_day_text.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.txt_project_state.setTextColor(mContext.getResources().getColor(R.color.text_989898));
                    holder.v_safe_line.setBackgroundColor(mContext.getResources().getColor(R.color.text_989898));

                    holder.txt_project_state.setText("已还款");
                }
            }
        }
        return convertView;
    }

    public class ViewHolder {
        private MySeekBar sb_progress;
        TextView tv_good_second_project_name, tv_good_second_project_rate_first, tv_good_second_project_rate_second, tv_good_second_project_span_chain, baifenhao;
        TextView txt_hot, txt_project_state;
        TextView tv_day_text;
        View v_safe_line;
        LinearLayout linearLayout;
    }


    //调用函数缩小图片
    public BitmapDrawable getNewDrawable(Context context, int restId, int dstWidth, int dstHeight) {
        Bitmap Bmp = BitmapFactory.decodeResource(
                context.getResources(), restId);
        Bitmap bmp = Bmp.createScaledBitmap(Bmp, dstWidth, dstHeight, true);
        BitmapDrawable d = new BitmapDrawable(bmp);
        Bitmap bitmap = d.getBitmap();
        if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
            d.setTargetDensity(context.getResources().getDisplayMetrics());
        }
        return d;
    }
}
