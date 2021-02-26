package com.basselop.neccesarystuff

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class MyHolder<T : Any, VB : ViewDataBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
    lateinit var lastData: T
}

/**
 * @param items list of shit
 * @param childResid the layout that is inflated for the children
 * @param onClick onClicklistener. optional
 * @param onBind bind function
 */
inline fun <T : Any, Vb : ViewDataBinding> RecyclerView.setup(
    items: List<T>,
    crossinline factory: (LayoutInflater, ViewGroup, Boolean) -> Vb,
    crossinline onClick: (RecyclerView.ViewHolder, T) -> Unit = { _, _ ->},
    crossinline onBind: (MyHolder<T, Vb>, T) -> Unit
) {
    adapter = object : RecyclerView.Adapter<MyHolder<T, Vb>>() {
        override fun getItemCount(): Int = items.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder<T, Vb> {
            val vb = factory(LayoutInflater.from(context), this@setup, false)
            return MyHolder<T, Vb>(vb).apply {
                vb.root.setOnClickListener {
                    onClick(this, lastData)
                }
            }
        }

        override fun onBindViewHolder(holder: MyHolder<T, Vb>, position: Int) {
            val t = items[position]
            holder.lastData = t
            onBind(holder, t)
        }
    }
}
