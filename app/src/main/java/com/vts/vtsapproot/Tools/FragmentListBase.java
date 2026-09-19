package com.vts.vtsapproot.Tools;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public abstract class FragmentListBase<
        VM extends ViewModel,
        AD extends RecyclerView.Adapter<?>>
        extends FragmentBase {
    protected final Handler HideFloatingActionButtonsHandler = new Handler(Looper.getMainLooper());
    protected VM My_ViewModel;
    protected AD My_Adapter;
    protected MaterialButton MaterialButton_Search;
    protected LinearLayout Search_LinearLayout;
    protected TextInputEditText Search_TextInputEditText_SearchContent;
    protected MaterialButton Search_MaterialButton_ClearContent;
    protected MaterialButton Search_MaterialButton_DoSearchContent;
    protected SwipeRefreshLayout SwipeRefreshLayout;
    protected RecyclerView RecyclerView;
    protected FrameLayout FrameLayout_FAB;
    protected FloatingActionButton FloatingActionButton_GoToBot;
    protected FloatingActionButton FloatingActionButton_BackToTop;
    protected final Runnable HideFloatingActionButtons = () -> {
        if (FloatingActionButton_BackToTop != null) FloatingActionButton_BackToTop.hide();
        if (FloatingActionButton_GoToBot != null) FloatingActionButton_GoToBot.hide();
    };
    protected FloatingActionButton FloatingActionButton_Movable;

    @Override
    protected void applyLayoutInsets(Insets insets) {
        super.applyLayoutInsets(insets);
        if (getContext() == null) return;
        if (FrameLayout_FAB != null)
            FrameLayout_FAB.post(() -> FrameLayout_FAB.setPadding(0, 0, 0, (insets == null ? 0 : insets.bottom) + MyBottomMenuHeight));
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshFloatingButton();
    }

    @Override
    public void onPause() {
        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        super.onPause();
    }

    @Override
    public void onStop() {
        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        super.onDestroy();
    }


    protected void setupSearchEvents() {
        setupSearchEvents(
                MaterialButton_Search,
                Search_LinearLayout,
                Search_TextInputEditText_SearchContent,
                Search_MaterialButton_ClearContent,
                Search_MaterialButton_DoSearchContent,
                FloatingActionButton_Movable,
                RecyclerView
        );
    }


    @SuppressLint("ClickableViewAccessibility")
    protected void setupRecyclerViewEvents() {
        if (getContext() == null) return;
        if (FloatingActionButton_BackToTop != null) {
            FloatingActionButton_BackToTop.setOnClickListener(v -> {
                if (My_Adapter == null || RecyclerView == null) return;
                RecyclerView.post(() -> RecyclerView.scrollToPosition(0));
                HideFloatingActionButtonsHandler.postDelayed(this::RefreshFloatingButton, 150);
            });
        }
        if (FloatingActionButton_GoToBot != null) {
            FloatingActionButton_GoToBot.setOnClickListener(v -> {
                if (My_Adapter == null || RecyclerView == null) return;
                int lastIndex = My_Adapter.getItemCount() - 1;
                if (lastIndex > 0) {
                    RecyclerView.post(() -> {
                        RecyclerView.scrollToPosition(lastIndex);
                        RecyclerView.smoothScrollToPosition(lastIndex + 1);
                    });
                    HideFloatingActionButtonsHandler.postDelayed(this::RefreshFloatingButton, 150);
                }
            });
        }
        if (FloatingActionButton_Movable != null) {
            FloatingActionButton_Movable.post(() -> restoreSharedPreferences(FloatingActionButton_Movable, RecyclerView));
            FloatingActionButton_Movable.setOnTouchListener(new View.OnTouchListener() {
                private static final int MAX_CLICK_DURATION = 200; // ms
                private long startClickTime;
                private float dX, dY;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    View parent = (View) view.getParent();
                    float paddingTop, paddingStart, paddingEnd, paddingBottom;
                    if (RecyclerView != null) {
                        paddingTop = Math.max(55f, (float) RecyclerView.getPaddingTop());
                        paddingStart = Math.max(55f, (float) RecyclerView.getPaddingStart() + (float) RecyclerView.getPaddingLeft());
                        paddingEnd = Math.max(55f, (float) RecyclerView.getPaddingEnd() + (float) RecyclerView.getPaddingRight());
                        paddingBottom = Math.max(55f, (float) RecyclerView.getPaddingBottom());
                    } else {
                        paddingTop = 55f;
                        paddingStart = 55f;
                        paddingEnd = 55f;
                        paddingBottom = 55f;
                    }
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            if (SwipeRefreshLayout != null) {
                                SwipeRefreshLayout.setEnabled(false);
                            }
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (SwipeRefreshLayout != null) {
                                SwipeRefreshLayout.setEnabled(false);
                            }

                            float newX = event.getRawX() + dX;
                            float newY = event.getRawY() + dY;

                            // Giới hạn không cho nút bay ra khỏi màn hình (tùy chọn)
                            newX = Math.max(paddingStart, Math.min(newX, (float) parent.getWidth() - (float) view.getWidth() - paddingEnd));
                            newY = Math.max(paddingTop, Math.min(newY, (float) parent.getHeight() - (float) view.getHeight() - paddingBottom));

                            view.animate().x(newX).y(newY).setDuration(0).start();

                            break;

                        case MotionEvent.ACTION_UP:
                            if (SwipeRefreshLayout != null) {
                                if (RecyclerView != null) {
                                    SwipeRefreshLayout.setEnabled(!RecyclerView.canScrollVertically(-1));
                                }
                            }

                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                            if (clickDuration < MAX_CLICK_DURATION) {
                                view.performClick();
                            } else {
                                float finalX;
                                float finalY = view.getY();

                                // Kiểm tra xem nút đang ở nửa bên trái hay nửa bên phải màn hình
                                if (view.getX() + (view.getWidth() / 2f) < parent.getWidth() / 2f) {
                                    finalX = paddingStart; // Hút về cạnh trái
                                } else {
                                    finalX = parent.getWidth() - view.getWidth() - paddingEnd; // Hút về cạnh phải
                                }

                                view.animate()
                                        .x(finalX)
                                        .setDuration(400) // Thời gian trượt 0.4 giây cho mượt
                                        .setInterpolator(new OvershootInterpolator(0.8f))
                                        .withEndAction(() -> {
                                            // Lưu lại vị trí chuẩn sau khi đã neo
                                            saveSharedPreferences(FloatingActionButton_Movable, finalX, finalY);
                                        })
                                        .start();
                            }
                            break;
                    }
                    return true;
                }
            });
        }
        if (RecyclerView != null) {
            if (RecyclerView.getLayoutManager() instanceof GridLayoutManager manager) {
                manager.setSpanCount(((ActBase) requireContext()).MySpanCount);
                RecyclerView.requestLayout();
            }
            RecyclerView.clearOnScrollListeners();
            RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean isAtTop = !recyclerView.canScrollVertically(-1);
                    if (SwipeRefreshLayout != null) {
                        SwipeRefreshLayout.setEnabled(isAtTop);
                    }
                    if (FloatingActionButton_BackToTop != null) {
                        if (isAtTop) FloatingActionButton_BackToTop.hide();
                    }
                    boolean isAtBot = !recyclerView.canScrollVertically(1);
                    if (FloatingActionButton_GoToBot != null) {
                        if (isAtBot) FloatingActionButton_GoToBot.hide();
                    }
                    if (Math.abs(dy) > 5) {
                        RefreshFloatingButton();
                    }
                }
            });
        }
        if (SwipeRefreshLayout != null) {
            SwipeRefreshLayout.setOnRefreshListener(() -> {
                _CustomListEvents.setValue(new CustomListEvents<>(CustomListEvents.Type.NEEDREFRESHDATA, null));
                SwipeRefreshLayout.setRefreshing(false);
            });
        }
    }


    protected void RefreshDataOnScreen(boolean pNeedBackToTop) {
        if (getContext() == null) return;
        if (RecyclerView == null || My_Adapter == null) return;
        LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) RecyclerView.getLayoutManager();
        if (mLinearLayoutManager == null) return;
        try {
            RecyclerView.postDelayed(
                    () -> {
                        if (pNeedBackToTop) {
                            mLinearLayoutManager.scrollToPosition(0);
                            int lastVisible = mLinearLayoutManager.findLastVisibleItemPosition();
                            if (lastVisible != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                                My_Adapter.notifyItemRangeChanged(0, lastVisible + 1, "PAYLOAD_CAILUMMIA");
                            } else {
                                My_Adapter.notifyItemRangeChanged(0, Math.min(50, My_Adapter.getItemCount()), "PAYLOAD_CAILUMMIA");
                            }
                        } else {
                            int firstVisible = mLinearLayoutManager.findFirstVisibleItemPosition();
                            if (firstVisible == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                                firstVisible = 0;
                            }
                            int lastVisible = mLinearLayoutManager.findLastVisibleItemPosition();
                            if (lastVisible == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                                lastVisible = 0;
                            }
                            My_Adapter.notifyItemRangeChanged((firstVisible > 0 ? firstVisible - 1 : firstVisible), lastVisible + 1, "PAYLOAD_CAILUMMIA");
                        }
                    }
                    , 100
            );
        } catch (Exception ignored) {
        }
        RecyclerView.postDelayed(this::RefreshFloatingButton, 150);
    }

    protected void FocusDataOnScreen(int position) {
        if (getContext() == null) return;
        if (position < 0) return;
        if (RecyclerView == null || My_Adapter == null) return;
        LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) RecyclerView.getLayoutManager();
        if (mLinearLayoutManager == null) return;
        try {
            RecyclerView.postDelayed(
                    () -> {
                        int firstVisible = mLinearLayoutManager.findFirstVisibleItemPosition();
                        if (firstVisible == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                            firstVisible = 0;
                        }
                        int lastVisible = mLinearLayoutManager.findLastVisibleItemPosition();
                        if (lastVisible == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                            lastVisible = 0;
                        }
                        if (position >= firstVisible && position <= lastVisible) {
                            My_Adapter.notifyItemRangeChanged((firstVisible > 0 ? firstVisible - 1 : firstVisible), lastVisible + 1, "PAYLOAD_CAILUMMIA");
                        } else {
                            mLinearLayoutManager.scrollToPositionWithOffset(position, 0);
                            firstVisible = mLinearLayoutManager.findFirstVisibleItemPosition();
                            if (firstVisible == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                                firstVisible = 0;
                            }
                            lastVisible = mLinearLayoutManager.findLastVisibleItemPosition();
                            if (lastVisible == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                                lastVisible = 0;
                            }
                            My_Adapter.notifyItemRangeChanged((firstVisible > 0 ? firstVisible - 1 : firstVisible), lastVisible + 1, "PAYLOAD_CAILUMMIA");
                        }
                    }
                    , 100
            );
        } catch (Exception ignored) {
        }
        RecyclerView.postDelayed(this::RefreshFloatingButton, 150);
    }


    protected void RefreshFloatingButton() {
        if (getContext() == null) return;
        if (
                RecyclerView == null ||
                        My_Adapter == null ||
                        FloatingActionButton_BackToTop == null ||
                        FloatingActionButton_GoToBot == null
        ) return;

        LinearLayoutManager mManager = (LinearLayoutManager) RecyclerView.getLayoutManager();
        if (mManager == null) return;

        int total = My_Adapter.getItemCount();
        if (total == 0) {
            FloatingActionButton_BackToTop.hide();
            FloatingActionButton_GoToBot.hide();
            return;
        }

        boolean showTop = RecyclerView.canScrollVertically(-1);
        boolean showBot = RecyclerView.canScrollVertically(1);

        if (showTop) {
            FloatingActionButton_BackToTop.show(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    super.onShown(fab);
                    fab.setAlpha(0.75f);
                }
            });
            if (SwipeRefreshLayout != null) {
                SwipeRefreshLayout.setEnabled(false);
            }
        } else {
            FloatingActionButton_BackToTop.hide();
            if (SwipeRefreshLayout != null) {
                SwipeRefreshLayout.setEnabled(true);
            }
        }

        if (showBot) {
            FloatingActionButton_GoToBot.show(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    super.onShown(fab);
                    fab.setAlpha(0.75f);
                }
            });
        } else {
            FloatingActionButton_GoToBot.hide();
        }

        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        if (showTop || showBot) {
            HideFloatingActionButtonsHandler.postDelayed(HideFloatingActionButtons, 2000);
        }
    }

}
