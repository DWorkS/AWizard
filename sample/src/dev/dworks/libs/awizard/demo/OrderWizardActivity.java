package dev.dworks.libs.awizard.demo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import dev.dworks.libs.awizard.WizardActivity;

public class OrderWizardActivity extends WizardActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mWizardModel = new SandwichWizardModel(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard);
		setWizardModel(mWizardModel);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.order, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_cancel:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
