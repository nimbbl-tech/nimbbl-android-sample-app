package com.example.nimbbl.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nimbbl.R
import com.example.nimbbl.ui.NimbblNativePaymentActivity
import com.zl.nimbblpaycoresdk.models.Item
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_payment_type_auto_debit
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_upi_flow_mode_server_intent
import kotlinx.coroutines.DelicateCoroutinesApi

class FastPaymentModeAdapter(
    private val list: List<Item>,
    private val context: NimbblNativePaymentActivity
) :
    RecyclerView.Adapter<PaymentModeItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentModeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PaymentModeItemViewHolder(
            inflater,
            parent
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PaymentModeItemViewHolder, position: Int) {
        val item: Item = list[position]
        holder.bind(item, context)
    }
}

class PaymentModeItemViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_fast_item, parent, false)) {
    private var tvTitle: TextView? = null
    private var ivIcon: ImageView? = null
    private var rlContainer: RelativeLayout? = null


    init {
        tvTitle = itemView.findViewById(R.id.tv_title)
        ivIcon = itemView.findViewById(R.id.iv_icon)
        rlContainer = itemView.findViewById(R.id.rl_main_container)


    }

    @OptIn(DelicateCoroutinesApi::class)
    fun bind(item: Item, context: NimbblNativePaymentActivity) {
        tvTitle!!.text = item.sub_payment_name
        ivIcon?.let {
            Glide.with(context).load(item.logo_url)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(context.getDrawable(R.drawable.credit_card))
                .into(it)
        }
        rlContainer?.setOnClickListener {
            if (item.sub_payment_code.equals("lazypay") ||
                item.sub_payment_code.equals("icici_paylater") ||
                item.sub_payment_code.equals("olamoney_postpaid")) {
                if (item.extraInfo?.auto_debit_flow_possible.equals("yes")) {
                    context.payLaterCompletePayment(
                        item.sub_payment_name.toString(),
                        "",
                        "",
                        value_payment_type_auto_debit
                    )
                } else {
                    context.initiatePayment("fast", item)
                }
            }else if(item.payment_mode_code.equals("upi",true)) {
                if(item.flow.equals(value_upi_flow_mode_server_intent)) {
                    context.upiIntentCompletePayment(
                        item.payment_mode.toString(),
                        item.sub_payment_code.toString(),
                        value_upi_flow_mode_server_intent,
                        item.extraInfo?.app_package_name.toString()
                    )
                }else {
                    context.upiInitiatePayment(item.extraInfo?.vpa_id.toString())
                }
            } else {
                context.initiatePayment("fast", item)
            }

        }

    }
}