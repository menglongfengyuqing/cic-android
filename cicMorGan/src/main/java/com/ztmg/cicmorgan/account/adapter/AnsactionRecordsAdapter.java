package com.ztmg.cicmorgan.account.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.ztmg.cicmorgan.R;
import com.ztmg.cicmorgan.account.entity.AnsactionRecordsEntity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 交易记录
 *
 * @author pc
 */
public class AnsactionRecordsAdapter extends BaseAdapter {

    private Context mContext;
    private List<AnsactionRecordsEntity> ansactionRecordList;
    private String type;

    public AnsactionRecordsAdapter(Context context, List<AnsactionRecordsEntity> list, String type) {
        this.mContext = context;
        this.ansactionRecordList = list;
        this.type = type;
    }

    @Override
    public int getCount() {
        return ansactionRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return ansactionRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.fragment_item_ansaction_records, null);
            holder = new ViewHolder();
            holder.tv_type_name = (TextView) convertView.findViewById(R.id.tv_type_name);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_remain_sum_money = (TextView) convertView.findViewById(R.id.tv_remain_sum_money);
            //holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //        if (position == ansactionRecordList.size() - 1) {
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

        AnsactionRecordsEntity entity = (AnsactionRecordsEntity) getItem(position);
        holder.tv_date.setText(entity.getTranddate());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足
        String amountDot = decimalFormat.format(Double.parseDouble(entity.getAmount()));

        String balancemoneyDot = decimalFormat.format(Double.parseDouble(entity.getBalancemoney()));
        holder.tv_remain_sum_money.setText(balancemoneyDot);
        String str = null;

        if (type.equals("-1")) {//全部
            String investmentType = entity.getType();
            //判断是否流标
            String inouttype = entity.getInouttype();
            if (inouttype.equals("1")) {
                String remark = entity.getRemark();
                String mRemark = remark.substring(0, 2);
                if (mRemark.equals("流标")) {
                    str = "流标";
                } else {
                    str = "退款";
                }
            } else if (inouttype.equals("2")) {
                str = "出借";
            }
            if (investmentType.equals("0")) {//充值
                holder.tv_type_name.setText("充值");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }

            }
            if (investmentType.equals("1")) {//提现
                holder.tv_type_name.setText("提现");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
            if (investmentType.equals("2")) {//活期投资，没有投资按钮
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
                if (entity.getProjectProductType().equals("1")) {//1安心投2供应链
                    holder.tv_type_name.setText(entity.getName() + "(" + str + ")");
                } else if (entity.getProjectProductType().equals("2")) {
                    holder.tv_type_name.setText(entity.getName() + "(" + entity.getSn() + ")" + "(" + str + ")");
                }
            }
            if (investmentType.equals("3")) {//定期投资
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
                if (entity.getProjectProductType().equals("1")) {//1安心投2供应链
                    holder.tv_type_name.setText(entity.getName() + "(" + str + ")");
                } else if (entity.getProjectProductType().equals("2")) {
                    holder.tv_type_name.setText(entity.getName() + "(" + entity.getSn() + ")" + "(" + str + ")");
                }

            }
            if (investmentType.equals("4")) {//还利息
                holder.tv_type_name.setText("还利息");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
            if (investmentType.equals("5")) {//还本金
                holder.tv_type_name.setText("还本金");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
            //if(investmentType.equals("6")){//活期赎回
            //	holder.tv_type_name.setText("活期赎回记录");
            //}
            if (investmentType.equals("7")) {//活动返现
                holder.tv_type_name.setText("活动返现");//+
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
            if (investmentType.equals("8")) {//活期利息
                holder.tv_type_name.setText("活期利息");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
            if (investmentType.equals("9")) {//佣金
                holder.tv_type_name.setText("佣金");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
            if (investmentType.equals("10")) {//抵用券
                holder.tv_type_name.setText("抵用券");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
        } else if (type.equals("0")) {//充值
            holder.tv_type_name.setText("充值");
            if (!TextUtils.isEmpty(entity.getInouttype())) {
                if (entity.getInouttype().equals("1")) {
                    holder.tv_money.setText("+" + amountDot);
                    holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                } else if (entity.getInouttype().equals("2")) {
                    holder.tv_money.setText("-" + amountDot);
                    holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                }
            }
        } else if (type.equals("1")) {//提现
            holder.tv_type_name.setText("提现");
            if (!TextUtils.isEmpty(entity.getInouttype())) {
                if (entity.getInouttype().equals("1")) {
                    holder.tv_money.setText("+" + amountDot);
                    holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                } else if (entity.getInouttype().equals("2")) {
                    holder.tv_money.setText("-" + amountDot);
                    holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                }
            }
        } else if (type.equals("3")) {//投资  // tv_registered_capital.setText(data.getBorrowerRegisterAmount() != null ? data.getBorrowerRegisterAmount() : "---");
            //判断是否流标
            String inouttype = entity.getInouttype();
            if (inouttype.equals("1")) {
                String remark = entity.getRemark();
                String mRemark = remark.substring(0, 2);
                if (mRemark.equals("流标")) {
                    str = "流标";
                } else {
                    str = "退款";
                }
            } else if (inouttype.equals("2")) {
                str = "出借";
            }
            if (entity.getProjectProductType().equals("1")) {//1安心投2供应链
                holder.tv_type_name.setText(entity.getName() + "(" + str + ")");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            } else if (entity.getProjectProductType().equals("2")) {
                holder.tv_type_name.setText(entity.getName() + "(" + entity.getSn() + ")" + "(" + str + ")");
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
        } else if (type.equals("4")) {//还款
            if (entity.getType().equals("4")) {
                holder.tv_type_name.setText("还利息");//还利息记录
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            } else if (entity.getType().equals("5")) {
                holder.tv_type_name.setText("还本金");//还利息记录
                if (!TextUtils.isEmpty(entity.getInouttype())) {
                    if (entity.getInouttype().equals("1")) {
                        holder.tv_money.setText("+" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_a11c3f));
                    } else if (entity.getInouttype().equals("2")) {
                        holder.tv_money.setText("-" + amountDot);
                        holder.tv_money.setTextColor(mContext.getResources().getColor(R.color.text_00a166));
                    }
                }
            }
        }

        return convertView;
    }

    public class ViewHolder {
        private TextView tv_type_name, tv_date, tv_money, tv_remain_sum_money;
    }
}
