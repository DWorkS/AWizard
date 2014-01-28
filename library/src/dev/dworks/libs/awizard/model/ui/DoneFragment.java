package dev.dworks.libs.awizard.model.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import dev.dworks.libs.awizard.model.ReviewCallbacks;
import dev.dworks.libs.awizard.model.WizardModel;
import dev.dworks.libs.awizard.model.WizardModelCallbacks;
import dev.dworks.libs.awizard.model.page.Page;

public abstract class DoneFragment extends Fragment implements WizardModelCallbacks {
	
    private ReviewCallbacks mCallbacks;
    private WizardModel mWizardModel;
    private Bundle mAllData; 
    
    public DoneFragment() {
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof ReviewCallbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks");
        }

        mCallbacks = (ReviewCallbacks) activity;
        mWizardModel = mCallbacks.getWizardModel();
        mWizardModel.registerListener(this);
        onPageTreeChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        mWizardModel.unregisterListener(this);
    }

    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    @Override
    public void onPageDataChanged(Page changedPage) {
        Bundle bundle = new Bundle();
        for (Page page : mWizardModel.getCurrentPageSequence()) {
        	bundle.putAll(page.getData());
        }
        
        mAllData = bundle;
    }
    
    public Bundle getAllData() {
    	return mAllData;
	}
}