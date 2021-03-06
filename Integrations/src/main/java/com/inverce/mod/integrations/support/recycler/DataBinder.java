package com.inverce.mod.integrations.support.recycler;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inverce.mod.core.IM;
import com.inverce.mod.core.Ui;
import com.inverce.mod.core.functional.IFunction;
import com.inverce.mod.integrations.support.annotations.IBind;
import com.inverce.mod.integrations.support.annotations.MapValue;
import com.inverce.mod.integrations.support.annotations.MapValue.ToDrawable;
import com.inverce.mod.integrations.support.annotations.MapValue.ToDrawableRes;
import com.inverce.mod.integrations.support.annotations.MapValue.ToStringRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/10/2017.
 */
public class DataBinder<T> implements IBind<T, BindViewHolder> {
    ToView<String, ImageView> loadImage;

    List<IBind<T, BindViewHolder>> tasks;

    public Resources res() {
        return IM.resources();
    }

    public DataBinder() {
        loadImage = (u, i, p) -> {
            throw new IllegalStateException("Image processor not specified");
        };
        tasks = new ArrayList<>();
    }

    @NonNull
    public <V extends android.view.View> DataBinder<T> bind(@NonNull ToHolder<T> bind) {
        tasks.add((holder, item, position) -> bind.bind(item, holder, position));
        return this;
    }

    @NonNull
    public <V extends android.view.View> DataBinder<T> bind(@NonNull IFunction<BindViewHolder, V> view, @NonNull ToView<T, V> bind) {
        tasks.add((holder, item, position) -> {
            V v = view.apply(holder);
            if (v != null) bind.bind(item, v, position);
        });
        return this;
    }

    @NonNull
    public <V extends android.view.View> DataBinder<T> bind(@IdRes int res, @NonNull ToView<T, V> bind) {
        return bind(h -> h.get(res), bind);
    }

    @NonNull
    public DataBinder<T> bindText(@IdRes int id, @NonNull MapValue<T, String> map) {
        return bind(p -> (TextView) p.get(id), (item, view, position) -> view.setText(map.get(item)));
    }

    @NonNull
    public DataBinder<T> bindTextRes(@IdRes int id, @NonNull ToStringRes<T> map) {
        return bind(p -> (TextView) p.get(id), (item, view, position) -> view.setText(map.get(item)));
    }

    @NonNull
    public DataBinder<T> bindImageRes(@IdRes int id, @NonNull ToDrawableRes<T> map) {
        return bind(p -> (ImageView) p.get(id), (item, view, position) -> view.setImageResource(map.get(item)));
    }

    @NonNull
    public DataBinder<T> bindImage(@IdRes int id, @NonNull MapValue<T, String> map) {
        return bind(p -> (ImageView) p.get(id), (item, view, position) -> loadImage.bind(map.get(item), view, position));
    }

    @NonNull
    public DataBinder<T> bindBackgroundRes(@IdRes int id, @NonNull ToDrawableRes<T> map) {
        return bind(p -> p.get(id), (item, view, position) -> view.setBackgroundResource(map.get(item)));
    }

    @NonNull
    public DataBinder<T> bindBackground(@IdRes int id, @NonNull ToDrawable<T> map) {
        return bind(p -> p.get(id), (item, view, position) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(map.get(item));
            } else {
                view.setBackgroundDrawable(map.get(item));
            }
        });
    }

    @NonNull
    public DataBinder<T> bindVisibility(@IdRes int id, @NonNull MapValue<T, Boolean> map) {
        return bind(p -> p.get(id), (item, view, position) -> Ui.visible(view, map.get(item)));
    }

    @NonNull
    public DataBinder<T> bindOnClickListener(@IdRes int id, @NonNull MapValue<T, View.OnClickListener> map) {
        return bind(p -> p.get(id), (item, view, position) -> view.setOnClickListener(map.get(item)));
    }

    @Override
    public synchronized void onBindViewHolder(BindViewHolder holder, T item, int position) {
        for (IBind<T, BindViewHolder> bind : tasks) {
            bind.onBindViewHolder(holder, item, position);
        }
    }

    @NonNull
    public DataBinder setLoadImage(ToView<String, ImageView> loadImage) {
        this.loadImage = loadImage;
        return this;
    }
}
