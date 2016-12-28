package net.melove.app.chat.widget.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Created by lzan13 on 2016/11/28.
 * 自定义 RecyclerView 控件 Item 项动画
 */

public class MLItemAnimator extends RecyclerView.ItemAnimator {

    @Override public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
        return false;
    }

    @Override public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder,
            @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    @Override public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    @Override public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
            @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo,
            @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    @Override public void runPendingAnimations() {

    }

    @Override public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override public void endAnimations() {

    }

    @Override public boolean isRunning() {
        return false;
    }
}
