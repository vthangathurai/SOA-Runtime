package org.ebayopensource.turmeric.tools.codegen.proto;

import java.io.File;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import junit.framework.Assert;

import com.ebay.soaframework.tools.codegen.CodeGenContext;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.WSDLParserException;
import com.ebay.soaframework.tools.codegen.external.wsdl.parser.schema.SchemaType;
import com.ebay.soaframework.tools.codegen.fastserformat.FastSerFormatCodegenBuilder;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.ProtobufSchemaMapper;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.dotproto.DotProtoGenerator;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufField;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufFieldModifier;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufMessage;
import com.ebay.soaframework.tools.codegen.fastserformat.protobuf.model.ProtobufSchema;
import com.ebay.test.TestAnnotate;
import com.ebay.test.soaframework.tools.codegen.ProtobufSchemaMapperTestUtils;
import static org.junit.Assert.*;
import static com.ebay.soaframework.tools.codegen.fastserformat.protobuf.tag.ProtobufTagConstants.*;

public class ProtoFileBasedTagGeneratorTest
{
    private String [] WSDLS = new String [] {
            "AAIADecoderService_for_tags",
            "AdBillingService_for_tags",
            "AddressBookService_for_tags",
            "AddressVerificationService_for_tags",
            "AdGroupService_for_tags",
            "AdjustmentService_for_tags",
            "AdminApplicationCatalogService_for_tags",
            "AdminService_for_tags",
            "AnonymizerService_for_tags",
            "ApplicationAuthorizationService_for_tags",
            "ApplicationCatalogService_for_tags",
            "ApplicationLoggingService_for_tags",
            "APTopoBuilderService_for_tags",
            "ARService_for_tags",
            "AsacAdminService_for_tags",
            "AssertionsService_for_tags",
            "AuthenticationService_for_tags",
            "AuthorizationService_for_tags",
            "AutoCompleteQueryService_for_tags",
            "AVSMonitoringAlertsService_for_tags",
            "BCOut_for_tags",
            "BdxStatisticsService_for_tags",
            "BillingTransactionService_for_tags",
            "BlacklistService_for_tags",
            "BRService_for_tags",
            "BRS_for_tags",
            "BSADAdminService_for_tags",
            "BuildServiceService_for_tags",
            "BulkDataExchangeService_for_tags",
            "BuyerProtectionService_for_tags",
            "BuyingListService_for_tags",
            "BuyingService_for_tags",
            "CallTreeService_for_tags",
            "CalMetaDataService_for_tags",
            "CalPoolDataService_for_tags",
            "CalSearchService_for_tags",
            "CampaignService_for_tags",
            "CartService_for_tags",
            "CatalogContentService_for_tags",
            "CatalogLanguageProcessingService_for_tags",
            "CatalogMatchProductService_for_tags",
            "CatalogService_for_tags",
            "CategoryServiceInternal_for_tags",
            "ChallengeService_for_tags",
            "ChargeService_for_tags",
            "CheckoutCartService_for_tags",
            "ClassifiedAdAdminService_for_tags",
            "ClassifiedAdManagementPrivateService_for_tags",
            "ClassifiedAdMetadataService_for_tags",
            "ClassifiedAdPeeringService_for_tags",
            "ClassifiedAdSearchService_for_tags",
            "ClassifiedAdUserPrivateService_for_tags",
            "CMDBService_for_tags",
            "CollectionsService_for_tags",
            "CommonMobileAppService_for_tags",
            "CommunityProductMediaService_for_tags",
            "CommunityProductService_for_tags",
            "CommunityProductUserAccountService_for_tags",
            "ConsumerService_for_tags",
            "ContactInfoUtilityService_for_tags",
            "CoreCatalogMetadataService_for_tags",
            "CoreProductService_for_tags",
            "CoreShippingService_for_tags",
            "CreativeService_for_tags",
            "CSPolicyDetailsService_for_tags",
            "CSUserPolicyViolationService_for_tags",
            "CTFKernelService_for_tags",
            "CurrencyRateService_for_tags",
            "DataNormalizerService_for_tags",
            "DataTransferService_for_tags",
            "DealsDataService_for_tags",
            "DealsService_for_tags",
            "DemoEightService_for_tags",
            "DemoNineService_for_tags",
            "DemoServiceChanService3_for_tags",
            "DemoService_for_tags",
            "DemoSevenService_for_tags",
            "DemoSix_for_tags",
            "DemoTenService_for_tags",
            "DeviceConfigurationService_for_tags",
            "DeviceIdentificationService_for_tags",
            "DPNewResolutionService_for_tags",
            "EasyReturnService_for_tags",
            "EbayDailyDealService_for_tags",
            "EbayDecoderService_for_tags",
            "EbayScheduledDealsService_for_tags",
            "EchoEaisService_for_tags",
            "EchoEciService_for_tags",
            "EchoEidpService_for_tags",
            "EimPeeringService_for_tags",
            "EmailMessagingRenderingService_for_tags",
            "EmailService_for_tags",
            "EntitlementService_for_tags",
            "EsamsBridgeService_for_tags",
            "ExperimentationExecutionService_for_tags",
            "ExperimentationManagementService_for_tags",
            "FeatureContingencyService_for_tags",
            "FeedbackService_for_tags",
            "FileTransferService_for_tags",
            "FindingDemandService_for_tags",
            "FindingMetadataService_for_tags",
            "FindingRecommendationService_for_tags",
            "FindingUserPreferenceService_for_tags",
            "FindingUserSettingsService_for_tags",
            "FindItemServiceCore_for_tags",
            "FindItemService_for_tags",
            "FindProductService1_for_tags",
            "FindProductService_for_tags",
            "FindStoreService_for_tags",
            "FundRaisingService_for_tags",
            "FundStatusServiceV1_for_tags",
            "GemDashboardService_for_tags",
            "GetNaturalSearchPageContentService_for_tags",
            "HalfIncentiveService_for_tags",
            "HalfRentalManagementService_for_tags",
            "ImageTextService_for_tags",
            "ImageTransformationService_for_tags",
            "ImageUtilityService_for_tags",
            "IncentiveService_for_tags",
            "IPhoneApplicationProcessService_for_tags",
            "ItemAccessoryClassificationService_for_tags",
            "ItemAccessoryClassificationSupportService_for_tags",
            "ItemClassificationPersistenceService_for_tags",
            "ItemClassificationService_for_tags",
            "ItemClassificationSupportService_for_tags",
            "ItemQualityClassificationService_for_tags",
            "ItemQualityClassificationSupportService_for_tags",
            "ItemService_for_tags",
            "ItemToProductAssociationService_for_tags",
            "ItemToProductSuggestionService_for_tags",
            "ItemToProductSuggestionSupportService_for_tags",
            "ListingDraftService_for_tags",
            "ListingDraftWorkspaceService_for_tags",
            "ListingTnSEvaluationService_for_tags",
            "ListService_for_tags",
            "MachineMetricAnalyzerService_for_tags",
            "MachineMetricDataService_for_tags",
            "MaestroDecisionService_for_tags",
            "MayThird_for_tags",
            "MetadataDependencyService_for_tags",
            "MobileDeviceNotificationService_for_tags",
            "MostSharedDataService_for_tags",
            "MyEbayApplicationService_for_tags",
            "MyGarageService_for_tags",
            "NewAnalyticService_for_tags",
            "NewARService_for_tags",
            "NewResolutionService_for_tags",
            "NewService_for_tags",
            "NormalizerDAO_for_tags",
            "NotificationEventMetadataService_for_tags",
            "NotificationsService_for_tags",
            "ObjectIdMapperService_for_tags",
            "OCSChannelService_for_tags",
            "ODRService_for_tags",
            "OpeneBayAdminService_for_tags",
            "OpeneBayApplicationIntegrationService_for_tags",
            "OpeneBayIdentityProviderService_for_tags",
            "OpeneBaySubscriptionService_for_tags",
            "OrderManagementService_for_tags",
            "PackageInsuranceService_for_tags",
            "PageJudgmentService_for_tags",
            "PageMetadataService_for_tags",
            "PageModelResolutionService_for_tags",
            "PaymentService_for_tags",
            "PersonalizationDataService_for_tags",
            "PgwService_for_tags",
            "PolicyEnforcementService_for_tags",
            "ProductBundleService_for_tags",
            "ProductCandidateService_for_tags",
            "ProductReviewService_for_tags",
            "ProductStatisticsService_for_tags",
            "QueryService_for_tags",
            "RadarEvaluationService_for_tags",
            "RateLimiterService_for_tags",
            "RegressService_for_tags",
            "RemedyService_for_tags",
            "RemotePortletSoa_for_tags",
            "RepositoryMetadataService_for_tags",
            "ResolutionCaseManagementServiceV1_for_tags",
            "ResolutionCaseManagementService_for_tags",
            "ResourcePersistenceService_for_tags",
            "RetrieveArtifactDirectly_for_tags",
            "RewardsAdminService_for_tags",
            "RewardsCSService_for_tags",
            "RewardsMemberService_for_tags",
            "RewardsService_for_tags",
            "RewardsTransactionService_for_tags",
            "RIMApplicationProcessService_for_tags",
            "RService_for_tags",
            "SafeMarkupService_for_tags",
            "SecurityAdminService_for_tags",
            "SellerDiscountOfferManagementService_for_tags",
            "SellerLimitsService_for_tags",
            "SellerPerformanceAdminService_for_tags",
            "SellerPerformanceService_for_tags",
            "ServiceManagerService_for_tags",
            "ShippingCalculatorServiceV2_for_tags",
            "ShippingEngineService_for_tags",
            "ShippingLabelService_for_tags",
            "ShippingRateService_for_tags",
            "ShippingRecommendationService_for_tags",
            "ShippingService_for_tags",
            "ShortURIService1_for_tags",
            "ShortURIService_for_tags",
            "SKUService_for_tags",
            "SOAMetricDAO_for_tags",
            "SOAMetricsQueryService_for_tags",
            "SolutionDirectoryService_for_tags",
            "StagingServiceService_for_tags",
            "StorageService_for_tags",
            "StorageStaasMgrServiceV1_for_tags",
            "StorageStaasMgrService_for_tags",
            "StoreInfoService_for_tags",
            "TaxCalculationService_for_tags",
            "TaxService_for_tags",
            "TestServiceForMailingConfirmation_for_tags",
            "TextGenomicsATORiskInfoService_for_tags",
            "ThirdPartyAuthConsentService_for_tags",
            "TnsTaxonomyService_for_tags",
            "TokenService_for_tags",
            "TrackingEventValidationService_for_tags",
            "TransactionFeedbackService_for_tags",
            "TranslationService1_for_tags",
            "TranslationService_for_tags",
            "UGCEnforcerService_for_tags",
            "UserAccountEntityService_for_tags",
            "UserAccountProcessService_for_tags",
            "UserAuthenticationService_for_tags",
            "UserContentClassificationService_for_tags",
            "UserEntityBlackListService_for_tags",
            "UserIncentivesService_for_tags",
            "UserProfileService_for_tags",
            "UserRiskService_for_tags",
            "UserService_for_tags",
            "UserVerificationService_for_tags",
            "UserVoiceAdminService_for_tags",
            "UserVoiceBaseService_for_tags",
            "UserVoiceCommonService_for_tags",
            "UserVoiceMetadataService_for_tags",
            "UserVoiceService_for_tags",
            "VehicleCatalogService_for_tags",
            "ViewItemCounterService_for_tags",
            "ViewItemService_for_tags",
            "WhitelistService_for_tags",
    		"ProtoTagMaster"};
    private String baseprotoPath = "tmp/meta-src/META-INF/soa/services/proto";

    public String[][] getWsdlArgs()
    {
        String testArgs[][] = new String[350][];
        int counter=0;
        for(String wsdl: WSDLS)
        {
            testArgs[counter] = new String[]{ "-servicename", wsdl, "-wsdl",
                    "UnitTests/src/com/ebay/test/soaframework/tools/codegen/data/proto/" + wsdl + ".wsdl", "-genType",
                    "ClientNoConfig", "-src", ".\\UnitTests\\src", "-dest", ".\\tmp", "-scv", "1.0.0", "-bin", ".\\bin",
                    "-enabledNamespaceFolding", "-nonXSDFormats", "protobuf" };    
            counter++;
            
        }

        return testArgs;
    }

    @Test
    @TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "tagGeneration", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
    public void testDotprotoGenerationForMasterWSDL() throws Exception
    {
        deleteOldProtoFiles();
        String[][] wsdlArgs = getWsdlArgs();
        for(String[] args : wsdlArgs)
        {
            if(args==null)
            {
                break;
            }
            CodeGenContext context = ProtobufSchemaMapperTestUtils.getCodeGenContext(args);
            FastSerFormatCodegenBuilder.getInstance().validateServiceIfApplicable(context);
            List<SchemaType> listOfSchemaTypes;
            try
            {
                listOfSchemaTypes = FastSerFormatCodegenBuilder.getInstance().generateSchema(context);
            }
            catch (WSDLParserException e)
            {
                Assert.fail("Generate Schema Failed." + e.getMessage());
                throw e;
            }

            ProtobufSchema schema = ProtobufSchemaMapper.getInstance().createProtobufSchema(listOfSchemaTypes, context);

            try
            {
                DotProtoGenerator.getInstance().generate(schema, context);
            }
            catch (Exception e1)
            {
                Assert.fail("Dot Proto generation failed." + e1.getMessage());
                throw e1;
            }
            ProtobufSchemaMapperTestUtils.validateTagNumberGeneration( context, schema );
        }

    }
    
    @Test
    @TestAnnotate(domainName = TestAnnotate.Domain.Services, feature = TestAnnotate.Feature.Codegen, subFeature = "tagGeneration", description = "", bugID = "", trainID = "", projectID = "", authorDev = "", authorQE = "")
    public void testProtoFileTagAssignments() throws Exception
    {
        for(String wsdlName : WSDLS)
        {
            String path = baseprotoPath + File.separator + wsdlName + File.separator + wsdlName + ".proto";
            System.out.println("Checking proto: " + path);
            List<ProtobufMessage> loadFindItemServiceManuallyWrittenProtoFile = ProtobufSchemaMapperTestUtils.loadFindItemServiceManuallyWrittenProtoFile(path);
            for(ProtobufMessage message : loadFindItemServiceManuallyWrittenProtoFile)
            {
                List<ProtobufField> fields = message.getFields();
                boolean [] allReqFields = new boolean[S_PROTO_OPTIONAL_TAG_START_NUMBER-1];
                for(ProtobufField field : fields)
                {
                    ProtobufFieldModifier fieldModifier = field.getFieldModifier();
                    int sequenceTagNumber = field.getSequenceTagNumber();
                    sequenceTagNumber--;
                    if(fieldModifier == ProtobufFieldModifier.REPEATED || fieldModifier == ProtobufFieldModifier.REQUIRED)
                    {
                        if(sequenceTagNumber<= S_PROTO_OPTIONAL_TAG_START_NUMBER-2)
                        {
                            allReqFields[sequenceTagNumber]=true;
                        }
                    }
                }
                boolean allRequredFieldsAssinged = true;
                for(boolean temp : allReqFields)
                {
                    allRequredFieldsAssinged &= temp;
                }
                Set<Integer> optionalTagSet = new TreeSet<Integer>();
                Set<Integer> repeatedTagSet = new TreeSet<Integer>();
                for(ProtobufField field : fields)
                {
                    ProtobufFieldModifier fieldModifier = field.getFieldModifier();
                    String fieldName = field.getFieldName();
                    int sequenceTagNumber = field.getSequenceTagNumber();
                    if(fieldModifier == ProtobufFieldModifier.REPEATED || fieldModifier == ProtobufFieldModifier.REQUIRED)
                    {
                        repeatedTagSet.add(sequenceTagNumber);
                        if(!allRequredFieldsAssinged)
                        {
                            if(sequenceTagNumber>S_PROTO_OPTIONAL_TAG_START_NUMBER-1)
                            {
                                Assert.fail("Required tag " + fieldName + " has value " + sequenceTagNumber );
                            }
                        }
                    }
                    else
                    {
                        optionalTagSet.add(sequenceTagNumber);
                        if(sequenceTagNumber<S_PROTO_OPTIONAL_TAG_START_NUMBER)
                        {
                            Assert.fail("Optional tag " + fieldName + " has value " + sequenceTagNumber );                            
                        }
                    }
                }
                System.out.println("Verifying " + message.getMessageName());
                boolean optionalContinuityPassed = verifyOverallContinuity(optionalTagSet, ProtobufFieldModifier.OPTIONAL);
                boolean repeatedContinuityPassed = verifyOverallContinuity(repeatedTagSet, ProtobufFieldModifier.REPEATED);
                if(!(optionalContinuityPassed&&repeatedContinuityPassed))
                {
                    repeatedTagSet.addAll(optionalTagSet);
                    boolean verifyContinuity = verifyContinuity(repeatedTagSet);
                    if(!verifyContinuity)
                    {
                        fail("Tags are not continuous");
                    }
                }
            }
        }        
    }
    
    private boolean verifyOverallContinuity(Set<Integer> tags, ProtobufFieldModifier fieldModifier )
    {
        if(tags.size()>0)
        {
            Integer next = tags.iterator().next();
            if(fieldModifier != ProtobufFieldModifier.OPTIONAL)
            {
                Assert.assertTrue(next.equals(S_PROTO_REQUIRED_TAG_START_NUMBER));
            }
        }
        return verifyContinuity(tags);
    }
    
    private boolean verifyContinuity(Set<Integer> tags)
    {
        int lastNum=Integer.MIN_VALUE;
        for(int num : tags)
        {
            if(lastNum == Integer.MIN_VALUE)
            {
                lastNum = num;
            }
            else
            {
                int result = num-lastNum;
                if(result != 1)
                {
                    return false;
                } 
                lastNum = num;
            }
        }
        return true;
    }
    
    private void deleteOldProtoFiles()
    {
        File file = new File(baseprotoPath);
        if(file.exists())
        {
            String[] list = file.list();    
            for(String dirPath : list)
            {
                File dirProto = new File(baseprotoPath + File.separator + dirPath);
                if(dirProto.exists())
                {
                    File[] listFiles = dirProto.listFiles();
                    for(File protoFile : listFiles)
                    {
                        protoFile.delete();
                    }
                    boolean delete = dirProto.delete();
                    System.out.println("Deleted " + dirPath + " " + delete);
                }
            }
        }
        
    }

}
