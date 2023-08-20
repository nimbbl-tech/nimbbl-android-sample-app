package com.example.nimbbl.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nimbbl.ui.NimbblNativePaymentMethodsActivity
import com.zl.nimbblpaycoresdk.models.Item
import com.zl.nimbblpaycoresdk.utils.PayloadKeys.Companion.value_upi_flow_mode_server_intent
import kotlinx.coroutines.DelicateCoroutinesApi
import tech.nimbbl.example.R

class FastPaymentModeAdapter(
    private val list: List<Item>,
    private val context: NimbblNativePaymentMethodsActivity
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
    private var tvSubTitle: TextView? = null
    private var ivIcon: ImageView? = null
    private var rlContainer: RelativeLayout? = null


    init {
        tvTitle = itemView.findViewById(R.id.tv_title)
        tvSubTitle = itemView.findViewById(R.id.tv_sub_title)
        ivIcon = itemView.findViewById(R.id.iv_icon)
        rlContainer = itemView.findViewById(R.id.rl_main_container)


    }

    @OptIn(DelicateCoroutinesApi::class)
    fun bind(item: Item, context: NimbblNativePaymentMethodsActivity) {
        tvTitle!!.text = item.sub_payment_name
        if(item.extraInfo != null  && item.extraInfo!!.vpa_id != null && item.extraInfo!!.vpa_id!!.isNotEmpty()) {
            tvSubTitle!!.visibility = View.VISIBLE
            tvSubTitle!!.text = item.extraInfo?.vpa_id
        }else{
            tvSubTitle!!.visibility = View.GONE
        }
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
                    ivIcon?.let { it1 ->
                        context.payLaterCompletePayment(
                            item.sub_payment_name.toString(),
                            "", false,
                            it1.drawable
                        )
                    }
                } else {
                    context.initiatePayment("fast", item)
                }
            }else if(item.payment_mode_code.equals("upi",true)) {
                if(item.flow.equals(value_upi_flow_mode_server_intent)) {
                    context.upiIntentCompletePayment(
                        item.payment_mode.toString(),
                        item.sub_payment_code.toString(),
                        value_upi_flow_mode_server_intent,
                        item.extraInfo?.app_package_name.toString(),
                        ivIcon!!.drawable
                    )
                }else {
                    (ivIcon?.drawable ?: ivIcon?.context?.getDrawable(R.drawable.bhim))?.let { it1 ->
                        context.upiInitiatePayment( item.sub_payment_name.toString(),item.extraInfo?.vpa_id.toString(),
                            it1
                        )
                    }
                }
            } else {
                context.initiatePayment("fast", item)
            }

        }

    }
}