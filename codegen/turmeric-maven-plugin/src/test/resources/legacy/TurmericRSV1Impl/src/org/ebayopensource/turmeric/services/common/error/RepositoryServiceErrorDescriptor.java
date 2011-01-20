/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.services.common.error;

import com.ebay.kernel.CodeGenerated;

import org.ebayopensource.turmeric.common.v1.types.ErrorCategory;
import org.ebayopensource.turmeric.common.v1.types.ErrorSeverity;
import org.ebayopensource.turmeric.services.common.error.ServiceBaseErrorDescriptor;


/**
 * Please DONOT EDIT/CHECKIN this file. If you want to add new Errors to this file
 * please reserve the error in Entity File RepositoryServiceErrorDescriptor.xml 
 *   
 * For more information please see wiki - http://wiki.arch.ebay.com/index.php?page=SOAErrorDescriptors
 * 
 * @author codegen
 */
public final class RepositoryServiceErrorDescriptor extends ServiceBaseErrorDescriptor implements CodeGenerated{

	
	private static final long serialVersionUID = 1L;
	private static final String SUB_DOMAIN = "RepositoryService";

	private RepositoryServiceErrorDescriptor(long errorId, String errorName, String subDomain, ErrorSeverity errorSeverity, 
			ErrorCategory category, String message){
		super(errorId, errorName, subDomain, errorSeverity, category, message);
	}
 
	public static final RepositoryServiceErrorDescriptor INVALID_ASSET_KEY = new RepositoryServiceErrorDescriptor(
			1,
			"INVALID_ASSET_KEY",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset key is invalid");
	public static final RepositoryServiceErrorDescriptor BASE_ASSETINFO_MISSING = new RepositoryServiceErrorDescriptor(
			2,
			"BASE_ASSETINFO_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"BaseAssetInfo is missing");
	public static final RepositoryServiceErrorDescriptor ASSET_NAME_MISSING = new RepositoryServiceErrorDescriptor(
			3,
			"ASSET_NAME_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset name is missing");
	public static final RepositoryServiceErrorDescriptor ASSET_VERSION_MISSING = new RepositoryServiceErrorDescriptor(
			4,
			"ASSET_VERSION_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset version is missing");
	public static final RepositoryServiceErrorDescriptor ASSET_NAMESPACE_MISSING = new RepositoryServiceErrorDescriptor(
			5,
			"ASSET_NAMESPACE_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset namespace is missing");
	public static final RepositoryServiceErrorDescriptor ASSET_ID_MISSING = new RepositoryServiceErrorDescriptor(
			6,
			"ASSET_ID_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset id is not provided in input data");
	public static final RepositoryServiceErrorDescriptor LIBRARY_ID_MISSING = new RepositoryServiceErrorDescriptor(
			7,
			"LIBRARY_ID_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Library id is not provided in input data");
	public static final RepositoryServiceErrorDescriptor UNKNOWN_EXCEPTION = new RepositoryServiceErrorDescriptor(
			8,
			"UNKNOWN_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Unknown Exception : An exception of the class {0} occurred in the following location\n File : {1},\n Class : {2},\n Method : {3},\n Line No : {4} \n The error message is : {5}");
	public static final RepositoryServiceErrorDescriptor DEPTH_MISSING = new RepositoryServiceErrorDescriptor(
			9,
			"DEPTH_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Depth is not provided in input data");
	public static final RepositoryServiceErrorDescriptor NO_ARTIFACTS_FOR_ADDING = new RepositoryServiceErrorDescriptor(
			10,
			"NO_ARTIFACTS_FOR_ADDING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"No artifacts provided for adding/modifying in input data");
	public static final RepositoryServiceErrorDescriptor DATE_CONVERTION_EXCEPTION_XMLGREGORIANDATE = new RepositoryServiceErrorDescriptor(
			11,
			"DATE_CONVERTION_EXCEPTION_XMLGREGORIANDATE",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Error converting java.util.Date to XmlGregorianDate");
	public static final RepositoryServiceErrorDescriptor NO_REQUEST_FOR_ASSET = new RepositoryServiceErrorDescriptor(
			12,
			"NO_REQUEST_FOR_ASSET",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"No active or inactive requests exist for this asset");
	public static final RepositoryServiceErrorDescriptor ASSET_NAME_AND_ID_MISSING = new RepositoryServiceErrorDescriptor(
			13,
			"ASSET_NAME_AND_ID_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Either one of asset id and asset name must be provided in input data");
	public static final RepositoryServiceErrorDescriptor ASSET_TYPE_MISSING = new RepositoryServiceErrorDescriptor(
			14,
			"ASSET_TYPE_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset type is missing");
	public static final RepositoryServiceErrorDescriptor ASSETSOURCE_EXCEPTION = new RepositoryServiceErrorDescriptor(
			15,
			"ASSETSOURCE_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The AssetSource operation has failed");
	public static final RepositoryServiceErrorDescriptor ASSET_LOCK_EXCEPTION = new RepositoryServiceErrorDescriptor(
			16,
			"ASSET_LOCK_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Exception occurred while trying to lock asset. Asset Could not be locked");
	public static final RepositoryServiceErrorDescriptor INVALID_VALUE_EXCEPTION = new RepositoryServiceErrorDescriptor(
			17,
			"INVALID_VALUE_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An invalid value was specified on the set methods of Asset");
	public static final RepositoryServiceErrorDescriptor ASSET_STATE_EXCEPTION = new RepositoryServiceErrorDescriptor(
			18,
			"ASSET_STATE_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset is not in a valid state to be altered. This usually happens when an Asset is read-only. Details :{0}");
	public static final RepositoryServiceErrorDescriptor AUTOMATION_EXCEPTION = new RepositoryServiceErrorDescriptor(
			19,
			"AUTOMATION_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The automation operation has failed");
	public static final RepositoryServiceErrorDescriptor ASSET_NOT_FOUND_EXCEPTION = new RepositoryServiceErrorDescriptor(
			20,
			"ASSET_NOT_FOUND_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset with the asset Id : {0} was not found");
	public static final RepositoryServiceErrorDescriptor ASSET_TYPE_EXCEPTION = new RepositoryServiceErrorDescriptor(
			21,
			"ASSET_TYPE_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The Requested Asset is not of type Service");
	public static final RepositoryServiceErrorDescriptor ASSET_UNLOCK_EXCEPTION = new RepositoryServiceErrorDescriptor(
			22,
			"ASSET_UNLOCK_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Exception occurred while trying to unlock asset. Asset Could not be unlocked");
	public static final RepositoryServiceErrorDescriptor OVERWRITE_EXCEPTION = new RepositoryServiceErrorDescriptor(
			23,
			"OVERWRITE_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Cannot overwrite the given value");
	public static final RepositoryServiceErrorDescriptor IO_EXCEPTION = new RepositoryServiceErrorDescriptor(
			24,
			"IO_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"IO Exception has occurred");
	public static final RepositoryServiceErrorDescriptor INVALID_LIBRARY = new RepositoryServiceErrorDescriptor(
			25,
			"INVALID_LIBRARY",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Library Id or name passed does not exist.");
	public static final RepositoryServiceErrorDescriptor SOURCE_ASSET_NOT_ENTERED = new RepositoryServiceErrorDescriptor(
			26,
			"SOURCE_ASSET_NOT_ENTERED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"the source for the relationships is not entered.");
	public static final RepositoryServiceErrorDescriptor RELATIONS_NOT_ENTERED = new RepositoryServiceErrorDescriptor(
			27,
			"RELATIONS_NOT_ENTERED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"There should be at least one relation to add.");
	public static final RepositoryServiceErrorDescriptor INVALID_VERSION = new RepositoryServiceErrorDescriptor(
			28,
			"INVALID_VERSION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The version number provided {0} is not of the valid format x.y.z, where x, y and z are whole numbers.");
	public static final RepositoryServiceErrorDescriptor DUPLICATE_ASSET = new RepositoryServiceErrorDescriptor(
			29,
			"DUPLICATE_ASSET",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An asset with the same name, version and asset type already exists.");
	public static final RepositoryServiceErrorDescriptor ASSET_VALIDATION_FAILED = new RepositoryServiceErrorDescriptor(
			30,
			"ASSET_VALIDATION_FAILED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset validation failed. The asset is incomplete. Please complete the following : {0}.");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_STORAGE_IO_EXCEPTION = new RepositoryServiceErrorDescriptor(
			31,
			"ARTIFACT_STORAGE_IO_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"IO Exception occurred while adding artifact. Message : {0}.");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_NOT_FOUND_EXCEPTION = new RepositoryServiceErrorDescriptor(
			32,
			"ARTIFACT_NOT_FOUND_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Artifact with Id {0} not found for asset with Id {1}.");
	public static final RepositoryServiceErrorDescriptor ASSET_SOURCE_OPERATION_EXCEPTION = new RepositoryServiceErrorDescriptor(
			33,
			"ASSET_SOURCE_OPERATION_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"AssetSource operation has failed. Message : {0}");
	public static final RepositoryServiceErrorDescriptor INVALID_ASSET_VALUE_EXCEPTION = new RepositoryServiceErrorDescriptor(
			34,
			"INVALID_ASSET_VALUE_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An Invalid value was tried to be set for an asset. Message : {0}");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_EMPTY_EXCEPTION = new RepositoryServiceErrorDescriptor(
			35,
			"ARTIFACT_EMPTY_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The artifact {0} did not have a pay-load/ value.");
	public static final RepositoryServiceErrorDescriptor ASSET_CANNOT_BEREVERTED = new RepositoryServiceErrorDescriptor(
			36,
			"ASSET_CANNOT_BEREVERTED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset is not in a state to be reverted in case of errors.");
	public static final RepositoryServiceErrorDescriptor RELATIONSHIP_NAME_NOT_ENTERED = new RepositoryServiceErrorDescriptor(
			37,
			"RELATIONSHIP_NAME_NOT_ENTERED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Relationship name a required value is not entered.");
	public static final RepositoryServiceErrorDescriptor RELATION_DELETION_ERROR = new RepositoryServiceErrorDescriptor(
			38,
			"RELATION_DELETION_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Error occurred while deleting a relation. Error Message : {0} ");
	public static final RepositoryServiceErrorDescriptor INVALID_RELATION_ERROR = new RepositoryServiceErrorDescriptor(
			39,
			"INVALID_RELATION_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An invalid relation was tried to be added. Error Message : {0} ");
	public static final RepositoryServiceErrorDescriptor BASIC_SERVICE_INFO_MISSING = new RepositoryServiceErrorDescriptor(
			40,
			"BASIC_SERVICE_INFO_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Basic Service Info is missing ");
	public static final RepositoryServiceErrorDescriptor REQUEST_EMPTY = new RepositoryServiceErrorDescriptor(
			41,
			"REQUEST_EMPTY",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Empty request is not allowed for this operation.");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_NAME_MISSING = new RepositoryServiceErrorDescriptor(
			42,
			"ARTIFACT_NAME_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Artifact Name is a required field.");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_VALUE_TYPE_MISSING = new RepositoryServiceErrorDescriptor(
			43,
			"ARTIFACT_VALUE_TYPE_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Artifact Value Type is a required field.");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_CATEGORY_MISSING = new RepositoryServiceErrorDescriptor(
			44,
			"ARTIFACT_CATEGORY_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Artifact Category is a required field.");
	public static final RepositoryServiceErrorDescriptor LIBRARY_NAME_AND_ID_MISSING = new RepositoryServiceErrorDescriptor(
			45,
			"LIBRARY_NAME_AND_ID_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Either library name or id must be provided.");
	public static final RepositoryServiceErrorDescriptor INVALID_CLASSIFIER_VALUE = new RepositoryServiceErrorDescriptor(
			46,
			"INVALID_CLASSIFIER_VALUE",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The value {0} is invalid for the classifier {1}.");
	public static final RepositoryServiceErrorDescriptor NO_CAPTURETEMPLATES_ASSOCIATED_WITH_ASSETTYPE = new RepositoryServiceErrorDescriptor(
			47,
			"NO_CAPTURETEMPLATES_ASSOCIATED_WITH_ASSETTYPE",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The are no asset capture templates associated with the asset type {0}.");
	public static final RepositoryServiceErrorDescriptor INVALID_ASSETTYPE = new RepositoryServiceErrorDescriptor(
			48,
			"INVALID_ASSETTYPE",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"{0} is not a valid asset type.");
	public static final RepositoryServiceErrorDescriptor NAMESPACE_NOT_UPDATED = new RepositoryServiceErrorDescriptor(
			49,
			"NAMESPACE_NOT_UPDATED",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"Asset namespace was not added/ updated successfully");
	public static final RepositoryServiceErrorDescriptor ASSET_ID_NOT_NEEDED = new RepositoryServiceErrorDescriptor(
			50,
			"ASSET_ID_NOT_NEEDED",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"Asset Id is not needed in this operation");
	public static final RepositoryServiceErrorDescriptor NEW_ASSET_LOCK_EXCEPTION = new RepositoryServiceErrorDescriptor(
			51,
			"NEW_ASSET_LOCK_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The operation {0} failed while trying to unlock the asset due to the following reason : {1}");
	public static final RepositoryServiceErrorDescriptor ASSET_KEY_MISMATCH_ERROR = new RepositoryServiceErrorDescriptor(
			52,
			"ASSET_KEY_MISMATCH_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The assetkey given as {0} does not match the asset key given in {1}");
	public static final RepositoryServiceErrorDescriptor SOURCE_EDITING_OUT_OF_SCOPE = new RepositoryServiceErrorDescriptor(
			53,
			"SOURCE_EDITING_OUT_OF_SCOPE",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"The Relation between assets with Asset Id {0} and {1} cannot be edited as the source asset {0} is not under the scope of the asset being edited {2}.");
	public static final RepositoryServiceErrorDescriptor INVALID_CAPTURE_TEMPLATE = new RepositoryServiceErrorDescriptor(
			54,
			"INVALID_CAPTURE_TEMPLATE",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The capture template named : {0} is not valid for an asset of type : {1}");
	public static final RepositoryServiceErrorDescriptor EXTENDED_SERVICE_INFO_MISSING = new RepositoryServiceErrorDescriptor(
			55,
			"EXTENDED_SERVICE_INFO_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Extended service info is missing in the service request.");
	public static final RepositoryServiceErrorDescriptor SERVICE_DESIGN_TIME_INFO_MISSING = new RepositoryServiceErrorDescriptor(
			56,
			"SERVICE_DESIGN_TIME_INFO_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Service design-time info is missing in the service request.");
	public static final RepositoryServiceErrorDescriptor SERVICE_LAYER_MISSING = new RepositoryServiceErrorDescriptor(
			57,
			"SERVICE_LAYER_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Service layer is a required field for create service request.");
	public static final RepositoryServiceErrorDescriptor PUBLISHING_STATUS_MISSING = new RepositoryServiceErrorDescriptor(
			58,
			"PUBLISHING_STATUS_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Publishing status is a required field for create service request.");
	public static final RepositoryServiceErrorDescriptor SERVICE_TYPE_MISSING = new RepositoryServiceErrorDescriptor(
			59,
			"SERVICE_TYPE_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Service type is a required field for create service request.");
	public static final RepositoryServiceErrorDescriptor CAPTURE_TEMPLATE_NOT_RESOLVED = new RepositoryServiceErrorDescriptor(
			60,
			"CAPTURE_TEMPLATE_NOT_RESOLVED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Capture template for the key {0} could not be found.");
	public static final RepositoryServiceErrorDescriptor CAPTURE_TEMPLATE_VALUES_PROPERTIES_FILE_NOT_LOADED = new RepositoryServiceErrorDescriptor(
			61,
			"CAPTURE_TEMPLATE_VALUES_PROPERTIES_FILE_NOT_LOADED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The capture template values properties file {0} could not be loaded.");
	public static final RepositoryServiceErrorDescriptor LIBRARY_NAME_MISSING = new RepositoryServiceErrorDescriptor(
			62,
			"LIBRARY_NAME_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Library name is not provided in input data");
	public static final RepositoryServiceErrorDescriptor NO_PROJECTS_IN_LIBRARY = new RepositoryServiceErrorDescriptor(
			63,
			"NO_PROJECTS_IN_LIBRARY",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The library {0} does not have any projects in it.");
	public static final RepositoryServiceErrorDescriptor NOT_LOGGED_INTO_LIBRARY = new RepositoryServiceErrorDescriptor(
			64,
			"NOT_LOGGED_INTO_LIBRARY",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Could not log into the library {0}.");
	public static final RepositoryServiceErrorDescriptor NO_VALUE_FOR_SEARCH_ATTRIBUTE_WARNING = new RepositoryServiceErrorDescriptor(
			65,
			"NO_VALUE_FOR_SEARCH_ATTRIBUTE_WARNING",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"No value has been provided for the search classifier criteria {0}. This classifier criteria will not be included in the search.");
	public static final RepositoryServiceErrorDescriptor NO_NAME_FOR_SEARCH_ATTRIBUTE_WARNING = new RepositoryServiceErrorDescriptor(
			66,
			"NO_NAME_FOR_SEARCH_ATTRIBUTE_WARNING",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"A classifier criteria without a name was provided. This classifier criteria will not be included in the search.");
	public static final RepositoryServiceErrorDescriptor NO_CLASSIFIER_CONJUNCTION_PROVIDED_DEFAULT_AND_SELECTED_WARNING = new RepositoryServiceErrorDescriptor(
			67,
			"NO_CLASSIFIER_CONJUNCTION_PROVIDED_DEFAULT_AND_SELECTED_WARNING",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"A conjunction was not provided for the classifier criteria. Hence default AND conjunction was applied.");
	public static final RepositoryServiceErrorDescriptor NO_CLASSIFIER_CRITERIA_PROVIDED = new RepositoryServiceErrorDescriptor(
			68,
			"NO_CLASSIFIER_CRITERIA_PROVIDED",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"No classifier criteria found to filter search.");
	public static final RepositoryServiceErrorDescriptor ASSET_LONGDESCRIPTION_NOT_LOADED = new RepositoryServiceErrorDescriptor(
			69,
			"ASSET_LONGDESCRIPTION_NOT_LOADED",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"The long description could not be loaded for the asset : {0}.");
	public static final RepositoryServiceErrorDescriptor INVALID_CATEGORIZING_CLASSIFIER = new RepositoryServiceErrorDescriptor(
			70,
			"INVALID_CATEGORIZING_CLASSIFIER",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"The asset classifier could not be found for : {0}.");
	public static final RepositoryServiceErrorDescriptor MISSING_ASSETINFO = new RepositoryServiceErrorDescriptor(
			71,
			"MISSING_ASSETINFO",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The AssetInfo detail is missing in the request.");
	public static final RepositoryServiceErrorDescriptor INVALID_ROLE = new RepositoryServiceErrorDescriptor(
			72,
			"INVALID_ROLE",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The Role Specified for the asset request is not valid.");
	public static final RepositoryServiceErrorDescriptor APPROVAL_ROLE_MISSING = new RepositoryServiceErrorDescriptor(
			75,
			"APPROVAL_ROLE_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Approval Role is not provided in the input data");
	public static final RepositoryServiceErrorDescriptor REJECTION_ROLE_MISSING = new RepositoryServiceErrorDescriptor(
			76,
			"REJECTION_ROLE_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Rejection Role is not provided in the input data");
	public static final RepositoryServiceErrorDescriptor APPROVAL_INFO_MISSING = new RepositoryServiceErrorDescriptor(
			73,
			"APPROVAL_INFO_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Approval info is not provided ");
	public static final RepositoryServiceErrorDescriptor REJECTION_INFO_MISSING = new RepositoryServiceErrorDescriptor(
			74,
			"REJECTION_INFO_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Rejection info is not provided ");
	public static final RepositoryServiceErrorDescriptor SERVICE_EXCEPTION = new RepositoryServiceErrorDescriptor(
			77,
			"SERVICE_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Service Exception, a system or application that occurs while processing a message");
	public static final RepositoryServiceErrorDescriptor APPROVAL_EXCEPTION = new RepositoryServiceErrorDescriptor(
			78,
			"APPROVAL_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The request had already been approved/rejected ");
	public static final RepositoryServiceErrorDescriptor INVALID_ARTIFACT_CATEGORY = new RepositoryServiceErrorDescriptor(
			79,
			"INVALID_ARTIFACT_CATEGORY",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The artifact category {0} under asset {1} with version {2} is invalid.");
	public static final RepositoryServiceErrorDescriptor NEXT_LEVEL_ATTRIBUTE_MISSING = new RepositoryServiceErrorDescriptor(
			80,
			"NEXT_LEVEL_ATTRIBUTE_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The next level attribute is a required field.");
	public static final RepositoryServiceErrorDescriptor SESSION_EXPIRED = new RepositoryServiceErrorDescriptor(
			81,
			"SESSION_EXPIRED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The session has expired.");
	public static final RepositoryServiceErrorDescriptor ACCESS_DENIED = new RepositoryServiceErrorDescriptor(
			82,
			"ACCESS_DENIED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Access has been denied to the library {0}. Reason : {1}");
	public static final RepositoryServiceErrorDescriptor APPLICATION_NOT_CONFIGURED_PROPERLY = new RepositoryServiceErrorDescriptor(
			83,
			"APPLICATION_NOT_CONFIGURED_PROPERLY",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"A configuration error was thrown because of the following reason : {0}.");
	public static final RepositoryServiceErrorDescriptor AUTHENTICATION_ERROR = new RepositoryServiceErrorDescriptor(
			84,
			"AUTHENTICATION_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The following authentication error was thrown : {0}.");
	public static final RepositoryServiceErrorDescriptor SESSION_ERROR = new RepositoryServiceErrorDescriptor(
			85,
			"SESSION_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The following session error was thrown : {0}.");
	public static final RepositoryServiceErrorDescriptor ERROR_FETCHING_API = new RepositoryServiceErrorDescriptor(
			86,
			"ERROR_FETCHING_API",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"There was a n error fetching certain APIs \n Details : {0}.");
	public static final RepositoryServiceErrorDescriptor CONNECTION_ERROR = new RepositoryServiceErrorDescriptor(
			87,
			"CONNECTION_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The following error was encountered while trying to connect to the library {0} \n Error : {1}.");
	public static final RepositoryServiceErrorDescriptor ASSET_REQUEST_NOT_FOUND = new RepositoryServiceErrorDescriptor(
			88,
			"ASSET_REQUEST_NOT_FOUND",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset request with id {0} was not found for the asset with id {1}.");
	public static final RepositoryServiceErrorDescriptor ACCESS_ERROR = new RepositoryServiceErrorDescriptor(
			89,
			"ACCESS_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An access denied exception was raised. Details : {0}");
	public static final RepositoryServiceErrorDescriptor CONFIGURATION_ERROR = new RepositoryServiceErrorDescriptor(
			90,
			"CONFIGURATION_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"A configuration error occurred. Details : {0}");
	public static final RepositoryServiceErrorDescriptor ASSET_CREATION_ERROR = new RepositoryServiceErrorDescriptor(
			91,
			"ASSET_CREATION_ERROR",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An error occurred while creating the asset with name {0} ({1}) of type {2)in the library {3}. Details : {4}");
	public static final RepositoryServiceErrorDescriptor USER_COOKIE_NOT_SET = new RepositoryServiceErrorDescriptor(
			92,
			"USER_COOKIE_NOT_SET",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The user cookie was not set or could not be retrieved. Details : {0}");
	public static final RepositoryServiceErrorDescriptor PROCESS_REVERSAL_EXCEPTION = new RepositoryServiceErrorDescriptor(
			93,
			"PROCESS_REVERSAL_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An occured while trying to revert the operation {0} Details : {1}");
	public static final RepositoryServiceErrorDescriptor INVALID_INPUT_EXCEPTION = new RepositoryServiceErrorDescriptor(
			94,
			"INVALID_INPUT_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Input for the operation is invalid.");
	public static final RepositoryServiceErrorDescriptor PROJECT_NOT_FOUND = new RepositoryServiceErrorDescriptor(
			95,
			"PROJECT_NOT_FOUND",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Project with name {0} was not found.");
	public static final RepositoryServiceErrorDescriptor GROUP_NOT_FOUND = new RepositoryServiceErrorDescriptor(
			96,
			"GROUP_NOT_FOUND",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"OrgGroup with name {0} was not found.");
	public static final RepositoryServiceErrorDescriptor NO_RELATIONSHIP_CONJUNCTION_PROVIDED_DEFAULT_AND_SELECTED_WARNING = new RepositoryServiceErrorDescriptor(
			97,
			"NO_RELATIONSHIP_CONJUNCTION_PROVIDED_DEFAULT_AND_SELECTED_WARNING",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"A conjunction was not provided for the relationship criteria. Hence default AND conjunction was applied.");
	public static final RepositoryServiceErrorDescriptor TARGET_ASSETKEY_MISSING = new RepositoryServiceErrorDescriptor(
			98,
			"TARGET_ASSETKEY_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Target Assetkey is not present inside Relation object");
	public static final RepositoryServiceErrorDescriptor ASSET_NOT_LOCKED_EXCEPTION = new RepositoryServiceErrorDescriptor(
			103,
			"ASSET_NOT_LOCKED_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The asset is not locked under the user : {0} but under the user : {1} ");
	public static final RepositoryServiceErrorDescriptor ASSETKEY_MISSING = new RepositoryServiceErrorDescriptor(
			99,
			"ASSETKEY_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Assetkey object is missing");
	public static final RepositoryServiceErrorDescriptor LIBRARY_MISSING = new RepositoryServiceErrorDescriptor(
			100,
			"LIBRARY_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Library object is missing");
	public static final RepositoryServiceErrorDescriptor FLATTENED_RELATIONSHIP_MISSING = new RepositoryServiceErrorDescriptor(
			101,
			"FLATTENED_RELATIONSHIP_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"FlattenedRelationship object is missing");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_MISSING = new RepositoryServiceErrorDescriptor(
			102,
			"ARTIFACT_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Artifact object is missing");
	public static final RepositoryServiceErrorDescriptor NEW_RELATION_MISSING = new RepositoryServiceErrorDescriptor(
			104,
			"NEW_RELATION_MISSING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"New relation is missing within RelationForUpdate");
	public static final RepositoryServiceErrorDescriptor USER_NOT_FOUND = new RepositoryServiceErrorDescriptor(
			105,
			"USER_NOT_FOUND",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"User {0} was not found");
	public static final RepositoryServiceErrorDescriptor NEW_RELATIONS_NOT_ENTERED = new RepositoryServiceErrorDescriptor(
			106,
			"NEW_RELATIONS_NOT_ENTERED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"New relation was not provided for the existing relation between {0} and {1}. A new relation should be provided if deleteRelationship Flag is not set.");
	public static final RepositoryServiceErrorDescriptor NO_REQUEST_PARAM = new RepositoryServiceErrorDescriptor(
			107,
			"NO_REQUEST_PARAM",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Request parameter is not provided for the operation");
	public static final RepositoryServiceErrorDescriptor INVALID_ASSET_ID = new RepositoryServiceErrorDescriptor(
			108,
			"INVALID_ASSET_ID",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Asset id provided is invalid");
	public static final RepositoryServiceErrorDescriptor ASSET_SUBMISSION_FAILED = new RepositoryServiceErrorDescriptor(
			109,
			"ASSET_SUBMISSION_FAILED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An error occurred while submitting the asset with name {0}, version {1} and of type {2}");
	public static final RepositoryServiceErrorDescriptor CREATE_COMPLETE_ASSET_FAILED = new RepositoryServiceErrorDescriptor(
			110,
			"CREATE_COMPLETE_ASSET_FAILED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"An error occurred while creating the asset with name {0}, version {1} and of type {2}");
	public static final RepositoryServiceErrorDescriptor GROUP_NOT_PROVIDED = new RepositoryServiceErrorDescriptor(
			111,
			"GROUP_NOT_PROVIDED",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Org Group name not provided for asset {0}");
	public static final RepositoryServiceErrorDescriptor ARTIFACT_RETRIEVAL_IO_EXCEPTION = new RepositoryServiceErrorDescriptor(
			112,
			"ARTIFACT_RETRIEVAL_IO_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"IO Exception occurred while retrieving the artifact with id {0} for the asset with id {1}. Message : {3}.");
	public static final RepositoryServiceErrorDescriptor UNSUPPORTED_ENCODING = new RepositoryServiceErrorDescriptor(
			113,
			"UNSUPPORTED_ENCODING",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"The encoding type {0} is not supported. Message : {1}.");
	public static final RepositoryServiceErrorDescriptor COMMENT_TOO_LONG = new RepositoryServiceErrorDescriptor(
			114,
			"COMMENT_TOO_LONG",
			SUB_DOMAIN,
			ErrorSeverity.WARNING,
			ErrorCategory.APPLICATION,
			"The comment was longer than 1000 characters and hence was truncated to {0}");
	public static final RepositoryServiceErrorDescriptor SERVICE_PROVIDER_EXCEPTION = new RepositoryServiceErrorDescriptor(
			115,
			"SERVICE_PROVIDER_EXCEPTION",
			SUB_DOMAIN,
			ErrorSeverity.ERROR,
			ErrorCategory.APPLICATION,
			"Exception occurred while intializing Service Provider class. Message : {0}");

}
