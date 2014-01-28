/*
 * Copyright 2012 Roman Nurik
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

package dev.dworks.libs.awizard.model;

import dev.dworks.libs.awizard.model.page.Page;
import dev.dworks.libs.awizard.model.ui.ReviewFragment;


/**
 * Callback interface connecting {@link Page}, {@link ReviewFragment}, and wizard model container
 * objects (e.g. {@link dev.dworks.libs.awizard.wizard.WizardActivity}.
 */
public interface ReviewCallbacks {
    WizardModel getWizardModel();
    void onEditScreenAfterReview(String pageKey);
}