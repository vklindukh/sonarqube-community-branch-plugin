/*
 * Copyright (C) 2020 Michael Clarke
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.github.mc1arke.sonarqube.plugin;

import com.github.mc1arke.sonarqube.plugin.ce.CommunityBranchEditionProvider;
import com.github.mc1arke.sonarqube.plugin.ce.CommunityReportAnalysisComponentProvider;
import com.github.mc1arke.sonarqube.plugin.scanner.CommunityBranchConfigurationLoader;
import com.github.mc1arke.sonarqube.plugin.scanner.CommunityBranchParamsValidator;
import com.github.mc1arke.sonarqube.plugin.scanner.CommunityProjectBranchesLoader;
import com.github.mc1arke.sonarqube.plugin.scanner.CommunityProjectPullRequestsLoader;
import com.github.mc1arke.sonarqube.plugin.scanner.ScannerPullRequestPropertySensor;
import com.github.mc1arke.sonarqube.plugin.server.CommunityBranchFeatureExtension;
import com.github.mc1arke.sonarqube.plugin.server.CommunityBranchSupportDelegate;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.AlmSettingsWs;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.CountBindingAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.DeleteAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.DeleteBindingAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.GetBindingAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.ListAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.ListDefinitionsAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.azure.CreateAzureAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.azure.SetAzureBindingAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.azure.UpdateAzureAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.bitbucket.CreateBitBucketAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.bitbucket.SetBitbucketBindingAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.bitbucket.UpdateBitbucketAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.github.CreateGithubAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.github.SetGithubBindingAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.github.UpdateGitHubAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.gitlab.CreateGitlabAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.gitlab.SetGitlabBindingAction;
import com.github.mc1arke.sonarqube.plugin.server.pullrequest.ws.action.gitlab.UpdateGitlabAction;
import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.rule.Severity;
import org.sonar.core.config.PurgeConstants;
import org.sonar.core.extension.CoreExtension;

import static com.github.mc1arke.sonarqube.plugin.ce.pullrequest.AnalysisDetails.IMAGE_URL_BASE;
import static com.github.mc1arke.sonarqube.plugin.ce.pullrequest.AnalysisDetails.CODE_INSIGHT;
import static com.github.mc1arke.sonarqube.plugin.ce.pullrequest.AnalysisDetails.DIFF_DECORATION;
import static com.github.mc1arke.sonarqube.plugin.ce.pullrequest.AnalysisDetails.DIFF_DECORATION_CODE_SMELL;
import static com.github.mc1arke.sonarqube.plugin.ce.pullrequest.AnalysisDetails.DIFF_DECORATION_SEVERITY;
import static com.github.mc1arke.sonarqube.plugin.ce.pullrequest.AnalysisDetails.SUMMARY_DECORATION;

/**
 * @author Michael Clarke
 */
public class CommunityBranchPlugin implements Plugin, CoreExtension {


    @Override
    public String getName() {
        return "Community Branch Plugin";
    }

    @Override
    public void load(CoreExtension.Context context) {
        if (SonarQubeSide.COMPUTE_ENGINE == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(CommunityReportAnalysisComponentProvider.class, CommunityBranchEditionProvider.class);
        } else if (SonarQubeSide.SERVER == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(CommunityBranchFeatureExtension.class, CommunityBranchSupportDelegate.class,

                                  AlmSettingsWs.class, CountBindingAction.class, DeleteAction.class,
                                  DeleteBindingAction.class, ListAction.class, ListDefinitionsAction.class,
                                  GetBindingAction.class,

                                  CreateGithubAction.class, SetGithubBindingAction.class, UpdateGitHubAction.class,

                                  CreateAzureAction.class, SetAzureBindingAction.class, UpdateAzureAction.class,

                                  CreateBitBucketAction.class, SetBitbucketBindingAction.class,
                                  UpdateBitbucketAction.class,

                                  CreateGitlabAction.class, SetGitlabBindingAction.class, UpdateGitlabAction.class,

                /* org.sonar.db.purge.PurgeConfiguration uses the value for the this property if it's configured, so it only
                needs to be specified here, but doesn't need any additional classes to perform the relevant purge/cleanup
                */
                                  PropertyDefinition
                                          .builder(PurgeConstants.DAYS_BEFORE_DELETING_INACTIVE_BRANCHES_AND_PRS)
                                          .name("Number of days before purging inactive short living branches")
                                          .description(
                                                  "Short living branches are permanently deleted when there are no analysis for the configured number of days.")
                                          .category(CoreProperties.CATEGORY_HOUSEKEEPING)
                                          .subCategory(CoreProperties.SUBCATEGORY_GENERAL).defaultValue("30")
                                          .type(PropertyType.INTEGER).build(),

                                  PropertyDefinition
                                          .builder(IMAGE_URL_BASE)
                                          .name("Static content URL")
                                          .defaultValue("https://raw.githubusercontent.com/mc1arke/sonarqube-community-branch-plugin/master/src/main/resources/pr-decoration-images")
                                          .description("URL for the static content.")
                                          .category(CoreProperties.CATEGORY_GENERAL)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS)
                                          .type(PropertyType.STRING).build(),

                                  PropertyDefinition
                                          .builder(CODE_INSIGHT)
                                          .name("Enable Code Insight BitBucket Integration")
                                          .defaultValue(Boolean.TRUE.toString())
                                          .description("Enable Code Insight BitBucket Integration.")
                                          .category(CoreProperties.CATEGORY_GENERAL)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS)
                                          .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
                                          .type(PropertyType.BOOLEAN).build(),

                                  PropertyDefinition
                                          .builder(DIFF_DECORATION)
                                          .name("Enable PR Diff Decoration for BitBucket")
                                          .defaultValue(Boolean.TRUE.toString())
                                          .description("Enable PR Diff Decoration for BitBucket.")
                                          .category(CoreProperties.CATEGORY_GENERAL)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS)
                                          .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
                                          .type(PropertyType.BOOLEAN).build(),

                                  PropertyDefinition
                                          .builder(SUMMARY_DECORATION)
                                          .name("Enable PR Summary Decoration for BitBucket")
                                          .defaultValue(Boolean.TRUE.toString())
                                          .description("Enable Code Insight BitBucket Integration.")
                                          .category(CoreProperties.CATEGORY_GENERAL)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS)
                                          .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
                                          .type(PropertyType.BOOLEAN).build(),

                                  PropertyDefinition
                                          .builder(DIFF_DECORATION_CODE_SMELL)
                                          .name("Include Code Smell Issue Type into PR Diff Decoration for BitBucket")
                                          .defaultValue(Boolean.TRUE.toString())
                                          .description("Include Code Smell Issue Type into PR Diff Decoration for BitBucket.")
                                          .category(CoreProperties.CATEGORY_GENERAL)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS)
                                          .onQualifiers(Qualifiers.PROJECT)
                                          .type(PropertyType.BOOLEAN).build(),

                                  PropertyDefinition
                                          .builder(DIFF_DECORATION_SEVERITY)
                                          .name("Max Severity included into PR Diff Decoration for BitBucket")
                                          .defaultValue(Severity.INFO)
                                          .description("Max Severity included into PR Diff Decoration for BitBucket. Supported INFO, MINOR, MAJOR, CRITICAL, BLOCKER.")
                                          .category(CoreProperties.CATEGORY_GENERAL)
                                          .subCategory(CoreProperties.SUBCATEGORY_BRANCHES_AND_PULL_REQUESTS)
                                          .onQualifiers(Qualifiers.PROJECT)
                                          .options(Severity.ALL)
                                          .type(PropertyType.SINGLE_SELECT_LIST).build()
                                  );
        }

    }

    @Override
    public void define(Plugin.Context context) {


        if (SonarQubeSide.SCANNER == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(CommunityProjectBranchesLoader.class, CommunityProjectPullRequestsLoader.class,
                                  CommunityBranchConfigurationLoader.class, CommunityBranchParamsValidator.class,
                                  ScannerPullRequestPropertySensor.class);
        }
    }
}
