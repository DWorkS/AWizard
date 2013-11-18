package dev.dworks.lib.wizard.demo;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import dev.dworks.lib.wizard.WizardActivitySide;

public class OrderSideWizardActivity extends WizardActivitySide {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setWizardModel(new SandwichWizardModel(this));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.order, menu);
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
