/*
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

package dev.dworks.libs.awizard.demo;

import android.content.Context;
import dev.dworks.libs.awizard.model.PageList;
import dev.dworks.libs.awizard.model.WizardModel;
import dev.dworks.libs.awizard.model.page.BranchPage;
import dev.dworks.libs.awizard.model.page.CustomerInfoPage;
import dev.dworks.libs.awizard.model.page.MultipleFixedChoicePage;
import dev.dworks.libs.awizard.model.page.ReviewPage;
import dev.dworks.libs.awizard.model.page.SingleFixedChoicePage;

public class SandwichWizardModel extends WizardModel {
    public SandwichWizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        BranchPage branchPage = new BranchPage(this, "Order type");
        branchPage.addBranch("Sandwich",
                new SingleFixedChoicePage(this, "Bread")
                        .setChoices("White", "Wheat", "Rye", "Pretzel", "Ciabatta")
                        .setRequired(true),

                new MultipleFixedChoicePage(this, "Meats")
                        .setChoices("Pepperoni", "Turkey", "Ham", "Pastrami",
                                "Roast Beef", "Bologna"),

                new MultipleFixedChoicePage(this, "Veggies")
                        .setChoices("Tomatoes", "Lettuce", "Onions", "Pickles",
                                "Cucumbers", "Peppers"),

                new MultipleFixedChoicePage(this, "Cheeses")
                        .setChoices("Swiss", "American", "Pepperjack", "Muenster",
                                "Provolone", "White American", "Cheddar", "Bleu"),

                new BranchPage(this, "Toasted?")
                        .addBranch("Yes",
                                new SingleFixedChoicePage(this, "Toast time")
                                        .setChoices("30 seconds", "1 minute",
                                                "2 minutes"))
                        .addBranch("No")
                        .setValue("No"))

        .addBranch("Salad",
                new SingleFixedChoicePage(this, "Salad type")
                        .setChoices("Greek", "Caesar")
                        .setRequired(true),

                new SingleFixedChoicePage(this, "Dressing")
                        .setChoices("No dressing", "Balsamic", "Oil & vinegar",
                                "Thousand Island", "Italian")
                        .setValue("No dressing")
        ).setRequired(true);

        return new PageList(branchPage,
                new CustomerInfoPage(this, "Your info")
                        .setRequired(true),
                new ReviewPage(this, "Review")
        );
    }
}