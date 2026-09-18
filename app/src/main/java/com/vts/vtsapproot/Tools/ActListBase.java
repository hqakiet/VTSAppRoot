package com.vts.vtsapproot.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

public abstract class ActListBase<
        VM extends ViewModel,
        AD extends RecyclerView.Adapter<?>>
        extends ActBase {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (RecyclerView != null && RecyclerView.getLayoutManager() instanceof GridLayoutManager manager) {
                manager.setSpanCount(MySpanCount);
                RecyclerView.requestLayout();
            }
        } else {
            if (FloatingActionButton_Movable != null) {
                restoreSharedPreferences(FloatingActionButton_Movable);
            }
        }
        if (FloatingActionButton_Movable != null) {
            FloatingActionButton_Movable.postDelayed(() -> adjustFloatingButtonPositionWithAnimation(FloatingActionButton_Movable), 100);
        }
    }

    @Override
    protected void applyLayoutInsets() {
        super.applyLayoutInsets();
        if (FrameLayout_FAB != null)
            FrameLayout_FAB.post(() -> FrameLayout_FAB.setPadding(0, 0, 0, (MySystemBarInsets == null ? 0 : MySystemBarInsets.bottom) + MyBottomMenuHeight));
        if (FloatingActionButton_Movable != null) {
            FloatingActionButton_Movable.postDelayed(() -> adjustFloatingButtonPositionWithAnimation(FloatingActionButton_Movable), 100);
        }
    }

    @Override
    protected void onResume() {
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
    protected void onDestroy() {
        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        super.onDestroy();
    }


    protected void setupSearchEvents() {
        if (
                MaterialButton_Search != null
                        && Search_LinearLayout != null
                        && Search_TextInputEditText_SearchContent != null
                        && Search_MaterialButton_ClearContent != null
                        && Search_MaterialButton_DoSearchContent != null
        ) {
            MaterialButton_Search.setVisibility(View.GONE);
            MaterialButton_Search.addOnCheckedChangeListener(
                    (materialButton, b) -> {
                        if (b) {
                            Search_TextInputEditText_SearchContent.setEnabled(true);

                            if (!gvSystem.getApp_TietKiemPin()) {
                                Search_LinearLayout.setPivotY(0f);
                                Search_LinearLayout.setAlpha(0f);

                                Search_LinearLayout.setTranslationY(0f);
                                Search_LinearLayout.setScaleY(0f);

                                Search_LinearLayout.setVisibility(View.VISIBLE);
                                Search_LinearLayout.post(() -> Search_LinearLayout.animate()
                                        .scaleY(1f)
                                        .alpha(1f)
                                        .setDuration(300)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .start());
                            } else {
                                Search_LinearLayout.setVisibility(View.VISIBLE);
                            }

                            if (FloatingActionButton_Movable != null) {
                                FloatingActionButton_Movable.postDelayed(() -> adjustFloatingButtonPositionWithAnimation(FloatingActionButton_Movable), 100);
                            }
                        } else {
                            String oldValue = Objects.requireNonNull(Search_TextInputEditText_SearchContent.getText()).toString().trim();
                            Search_TextInputEditText_SearchContent.setText("");
                            if (!oldValue.isEmpty()) {
                                _CustomListEvents.setValue(new CustomListEvents<>(CustomListEvents.Type.NEEDSTOPFILTER, ""));
                            }
                            Search_TextInputEditText_SearchContent.setEnabled(false);

                            if (!gvSystem.getApp_TietKiemPin()) {
                                Search_LinearLayout.setPivotY(0f);

                                Search_LinearLayout.animate()
                                        .scaleY(0f)
                                        .alpha(0f)
                                        .setDuration(200)
                                        .setInterpolator(new AccelerateInterpolator())
                                        .withEndAction(() -> {
                                            Search_LinearLayout.setVisibility(View.GONE);
                                            Search_LinearLayout.setScaleY(1f);
                                        })
                                        .start();
                            } else {
                                Search_LinearLayout.setVisibility(View.GONE);
                            }

                            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(Search_TextInputEditText_SearchContent.getWindowToken(), 0);
                            if (FloatingActionButton_Movable != null) {
                                FloatingActionButton_Movable.postDelayed(() -> adjustFloatingButtonPositionWithAnimation(FloatingActionButton_Movable), 100);
                            }
                        }
                    }
            );
            Search_MaterialButton_ClearContent.setOnClickListener(new SingleClickListener() {
                @Override
                public void safeSingleClick(View v) {
                    String oldValue = Objects.requireNonNull(Search_TextInputEditText_SearchContent.getText()).toString().trim();
                    Search_TextInputEditText_SearchContent.setText("");
                    if (!oldValue.isEmpty()) {
                        _CustomListEvents.setValue(new CustomListEvents<>(CustomListEvents.Type.NEEDSTOPFILTER, ""));
                    }
                }
            });
            Search_MaterialButton_DoSearchContent.setOnClickListener(new SingleClickListener() {
                @Override
                public void safeSingleClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Search_TextInputEditText_SearchContent.getWindowToken(), 0);
                    _CustomListEvents.setValue(new CustomListEvents<>(CustomListEvents.Type.NEEDSTARTFILTER, Objects.requireNonNull(Search_TextInputEditText_SearchContent.getText()).toString().trim()));
                }
            });
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    protected void setupRecyclerViewEvents() {
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
            restoreSharedPreferences(FloatingActionButton_Movable);
            FloatingActionButton_Movable.postDelayed(() -> adjustFloatingButtonPositionWithAnimation(FloatingActionButton_Movable), 100);
            FloatingActionButton_Movable.setOnTouchListener(new View.OnTouchListener() {
                private static final int MAX_CLICK_DURATION = 200; // ms
                private long startClickTime;
                private float dX, dY;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
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
                            View parent = (View) view.getParent();
                            newX = Math.max(55f, Math.min(newX, (float) parent.getWidth() - (float) view.getWidth() - 55f));
                            newY = Math.max(55f+ (MySystemBarInsets == null ? 0f : (float) MySystemBarInsets.top), Math.min(newY, (float) parent.getHeight() - (float) view.getHeight() - 55f - (MySystemBarInsets == null ? 0f : (float) MySystemBarInsets.bottom) - (float) MyBottomMenuHeight));

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
                                // Lấy chiều rộng màn hình
                                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                                float finalX;

                                // Kiểm tra xem nút đang ở nửa bên trái hay nửa bên phải màn hình
                                if (view.getX() + (view.getWidth() / 2f) < screenWidth / 2f) {
                                    finalX = 55f; // Hút về cạnh trái
                                } else {
                                    finalX = screenWidth - view.getWidth() - 55f; // Hút về cạnh phải
                                }

                                view.animate()
                                        .x(finalX)
                                        .setDuration(200) // Thời gian trượt 0.4 giây cho mượt
                                        .setInterpolator(new DecelerateInterpolator())
                                        .withEndAction(() -> {
                                            // Lưu lại vị trí chuẩn sau khi đã neo
                                            saveSharedPreferences(FloatingActionButton_Movable, finalX, view.getY());
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


    protected void RefreshDataOnScreen() {
        RefreshDataOnScreen(true);
    }

    protected void RefreshDataOnScreen(boolean pNeedBackToTop) {
        if (RecyclerView == null || My_Adapter == null) return;
        LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) RecyclerView.getLayoutManager();
        if (mLinearLayoutManager == null) return;
        try {
            if (pNeedBackToTop) {
                RecyclerView.postDelayed(() -> {
                    RecyclerView.scrollToPosition(0);
                    int lastVisible = mLinearLayoutManager.findLastVisibleItemPosition();
                    if (lastVisible != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        My_Adapter.notifyItemRangeChanged(0, lastVisible + 1, "PAYLOAD_CAILUMMIA");
                    } else {
                        My_Adapter.notifyItemRangeChanged(0, Math.min(50, My_Adapter.getItemCount()), "PAYLOAD_CAILUMMIA");
                    }
                }, 100);
            } else {
                RecyclerView.postDelayed(() -> {
                    int firstVisible = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstVisible != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        RecyclerView.scrollToPosition(firstVisible);
                    }
                    int lastVisible = mLinearLayoutManager.findLastVisibleItemPosition();
                    if (firstVisible != androidx.recyclerview.widget.RecyclerView.NO_POSITION && lastVisible != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        My_Adapter.notifyItemRangeChanged((firstVisible > 0 ? firstVisible - 1 : firstVisible), lastVisible + 1, "PAYLOAD_CAILUMMIA");
                    } else {
                        My_Adapter.notifyItemRangeChanged((firstVisible > 0 ? firstVisible - 1 : firstVisible), Math.min(50, My_Adapter.getItemCount()), "PAYLOAD_CAILUMMIA");
                    }
                }, 100);
            }
        } catch (Exception ignored) {}
        RecyclerView.postDelayed(this::RefreshFloatingButton, 150);
    }

    protected void FocusDataOnScreen(int position) {
        if (RecyclerView == null || My_Adapter == null) return;
        LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) RecyclerView.getLayoutManager();
        if (mLinearLayoutManager == null) return;
        if (position < 0) return;
        try {
            RecyclerView.scrollToPosition(position);
            int lastVisible = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (lastVisible != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                My_Adapter.notifyItemRangeChanged(0, lastVisible + 1, "PAYLOAD_CAILUMMIA");
            } else {
                My_Adapter.notifyItemRangeChanged(0, Math.min(50, My_Adapter.getItemCount()), "PAYLOAD_CAILUMMIA");
            }
        } catch (Exception ignored) {}
        RecyclerView.postDelayed(this::RefreshFloatingButton, 150);
    }


    protected void RefreshFloatingButton() {
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
