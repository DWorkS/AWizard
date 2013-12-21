/*
 * Copyright 2012 Roman Nurik
 * Copyright 2013 Hari Krishna Dulipudi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.dworks.lib.wizard;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import dev.dworks.lib.wizard.model.PageFragmentCallbacks;
import dev.dworks.lib.wizard.model.ReviewCallbacks;
import dev.dworks.lib.wizard.model.WizardModel;
import dev.dworks.lib.wizard.model.WizardModelCallbacks;
import dev.dworks.lib.wizard.model.page.DonePage;
import dev.dworks.lib.wizard.model.page.Page;
import dev.dworks.lib.wizard.model.page.ReviewPage;
import dev.dworks.lib.wizard.model.ui.StepPagerStrip;
import dev.dworks.libs.actionbarplus.SherlockFragmentActivityPlus;

	/**
	 * @author HaKr
	 *
	 */
public class WizardActivity extends SherlockFragmentActivityPlus implements
        PageFragmentCallbacks,
        ReviewCallbacks,
        WizardModelCallbacks {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

	//The view pager for the wizard
    private ViewPager mPager;
    //FragmentStatePagerAdapter adapter for the wizard
    private PagerAdapter mPagerAdapter;

    //Set to true to edit data at review
    private boolean mEditingAfterReview;
    
    private String mReviewText = null;
    private String mDoneText = null;
    
	//The wizard model for the wizard
    protected WizardModel mWizardModel;

    private boolean mConsumePageSelectedEvent;

    //Next button in the wizard
    private Button mNextButton;
    //Previous button in the wizard
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
	private int mReviewPagePosition;
	private int mDonePagePosition;
	private int mOrientation = HORIZONTAL;
	private boolean mDataChanged = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);
        
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
            mDataChanged = savedInstanceState.getBoolean("dataChanged");
        }
    }

    private void ensureControls() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        if (mPager == null) {
            throw new RuntimeException(
                    "Your content must have a android.support.v4.view.ViewPager whose id attribute is " +
                    "'R.id.pager'");
        }
        //mPager.setOrientation(mOrientation);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });
        
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOrientation(mOrientation);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });
        
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size() - 1) {
                	onDoneClick();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mReviewPagePosition);
                    } else {
                    	if(mPager.getCurrentItem() == mReviewPagePosition){
                    		Page page = mCurrentPageSequence.get(mReviewPagePosition);
                    		page.getData().putBoolean(ReviewPage.PROCESS_DATA_KEY, true);
                    		page.notifyDataChanged();
                    	}
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });
        
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
        
        mDonePagePosition = getDonePagePosition();
        mReviewPagePosition = getReviewPagePosition();
        mStepPagerStrip.setReviewPagePosition(mReviewPagePosition);
        mStepPagerStrip.setDonePagePosition(mDonePagePosition);;
	}

	/**
	 * @param showReview the show review to set
	 */
	public final void setReviewText(String finish) {
		this.mReviewText = finish;
	}
	

	/**
	 * @param showReview the show review to set
	 */
	public final void setDoneText(String finish) {
		this.mDoneText = finish;
	}
	
	/**
	 * on Review next action
	 */
	public void onConfirmClick() {
		finish();
	}
	
	/**
	 * on Done next action
	 */
	public void onDoneClick() {
		finish();
	}
    
    @Override
    public final void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        updatePagerStrip();
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updatePagerStrip() {
    	int pageCount = mCurrentPageSequence.size();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(pageCount);
        mReviewPagePosition = getReviewPagePosition();
        mDonePagePosition = getDonePagePosition();
        mStepPagerStrip.setReviewPagePosition(mReviewPagePosition);
        mStepPagerStrip.setDonePagePosition(mDonePagePosition);;
	}

	private void updateBottomBar() {
    	if(null == mNextButton || null == mPrevButton){
    		return;
    	}
        TypedValue v = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
        int position = mPager.getCurrentItem();
        if (position == mReviewPagePosition) {
        	mReviewText = mReviewText != null ? mReviewText : getResources().getString(R.string.review_next);
            mNextButton.setText(mReviewText);
            mNextButton.setBackgroundResource(R.drawable.review_backgrounds);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        }else if (position == mDonePagePosition) {
        	mDoneText = mDoneText != null ? mDoneText : getResources().getString(R.string.done_next);
            mNextButton.setText(mDoneText);
            mNextButton.setBackgroundResource(R.drawable.done_backgrounds);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        }else if (position == mCurrentPageSequence.size() - 1) {
            mNextButton.setText(R.string.done_next);
            if(mDonePagePosition != -1){
                mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
                mNextButton.setTextAppearance(this, v.resourceId);
            }
            else{
            	mNextButton.setBackgroundResource(R.drawable.done_backgrounds);
            	mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
            }
        }else {
            mNextButton.setText(mEditingAfterReview ? R.string.review : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            mNextButton.setTextAppearance(this, v.resourceId);
            mPrevButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
        outState.putBoolean("dataChanged", mDataChanged);
    }

    @Override
    public final WizardModel getWizardModel() {
        return mWizardModel;
    }
    
    public final void setWizardModel(WizardModel wizardModel) {
    	mWizardModel = wizardModel;
        if (mWizardModel == null) {
            throw new RuntimeException("Wizard Model cannot be empty");
        }

        mWizardModel.registerListener(this);
        //TODO: Change in support lib causing null pointer
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        
        ensureControls();
        onPageTreeChanged();
    }
    
    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        switch (orientation) {
            case HORIZONTAL:
            case VERTICAL:
                break;

            default:
                throw new IllegalArgumentException("Only HORIZONTAL and VERTICAL are valid orientations.");
        }
        mOrientation = orientation;
        if(null != mStepPagerStrip){
        	mStepPagerStrip.setOrientation(mOrientation);
        }
    }

    @Override
    public final void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public final void onPageDataChanged(Page page) {
    	mDataChanged = true;
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }
    
    public boolean getDataChanged() {
        return mDataChanged;
    }

    private final int getReviewPagePosition() {
    	int pagePosition = -1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page instanceof ReviewPage) {
            	pagePosition = i;
                break;
            }
        }
        return pagePosition;
	}
    
    private final int getDonePagePosition() {
    	int pagePosition = -1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page instanceof DonePage) {
            	pagePosition = i;
                break;
            }
        }
        return pagePosition;
	}

    private final boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    /*
     * Adapter for fragments 
     * */
    private class PagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
/*            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }
*/
            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

		@Override
		public int getCount() {
			if (mCurrentPageSequence == null) {
				return 0;
			}
			return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() /* + 1 */);
		}

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}