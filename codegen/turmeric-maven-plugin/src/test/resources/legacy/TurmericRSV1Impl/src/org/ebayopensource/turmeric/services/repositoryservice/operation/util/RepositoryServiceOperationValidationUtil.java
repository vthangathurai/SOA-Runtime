/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.services.repositoryservice.operation.util;

import java.util.List;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.errorlibrary.repository.ErrorConstants;

import org.ebayopensource.turmeric.repository.v1.services.ArtifactInfo;
import org.ebayopensource.turmeric.repository.v1.services.AssetInfo;
import org.ebayopensource.turmeric.repository.v1.services.AssetInfoForUpdate;
import org.ebayopensource.turmeric.repository.v1.services.AssetKey;
import org.ebayopensource.turmeric.repository.v1.services.BasicAssetInfo;

import org.ebayopensource.turmeric.repository.v1.services.FlattenedRelationship;
import org.ebayopensource.turmeric.repository.v1.services.FlattenedRelationshipForUpdate;
import org.ebayopensource.turmeric.repository.v1.services.Library;
import org.ebayopensource.turmeric.repository.v1.services.Relation;
import org.ebayopensource.turmeric.repository.v1.services.RelationForUpdate;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.services.repositoryservice.impl.RepositoryServiceValidateUtil;



public class RepositoryServiceOperationValidationUtil {
	
	public static boolean validateAssetInfo(AssetInfo assetInfo, List<CommonErrorData> errorDataList)
	{
		boolean isValid = true;
		
		if(assetInfo != null)
		{			
			//validate basicAssetInfo
			isValid = RepositoryServiceOperationValidationUtil.validateBasicAssetInfo(assetInfo.getBasicAssetInfo(), errorDataList);
			
			//validate artifactInfo
			List<ArtifactInfo> artifactInfos = assetInfo.getArtifactInfo();
			if(artifactInfos.size() > 0)
			{					
				isValid = RepositoryServiceOperationValidationUtil.validateArtifactInfos(artifactInfos, errorDataList);
			}
			
			// validate FlattenedRelationship
			FlattenedRelationship flattenedRelationship = assetInfo.getFlattenedRelationship();
			if(flattenedRelationship != null) 
			{
				isValid = RepositoryServiceOperationValidationUtil.validateFlattenedRelationship(flattenedRelationship, errorDataList);
			}
		}
		else
		{
			isValid = false;
			errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.MISSING_ASSETINFO, ErrorConstants.ERRORDOMAIN));
		}
		
		return isValid;
	}
	
	public static boolean validateAssetInfoForUpdate(AssetInfoForUpdate assetInfoForUpdate, List<CommonErrorData> errorDataList)
	{
		boolean isValid = true;
		
		if(assetInfoForUpdate != null)
		{			
			//validate basicAssetInfo
			isValid = validateBasicAssetInfo(assetInfoForUpdate.getBasicAssetInfo(), errorDataList);
			
			//validate artifactInfo
			List<ArtifactInfo> artifactInfos = assetInfoForUpdate.getArtifactInfo();
			if(artifactInfos.size() > 0)
			{					
				isValid = validateArtifactInfos(artifactInfos, errorDataList);
			}
			
			// validate FlattenedRelationshipForUPdate
			FlattenedRelationshipForUpdate flattenedRelationshipForUpdate = assetInfoForUpdate.getFlattenedRelationshipForUpdate();
			if(flattenedRelationshipForUpdate != null) 
			{
				isValid = validateFlattenedRelationshipForUpdate(flattenedRelationshipForUpdate, errorDataList);
			}
		}
		else
		{
			isValid = false;
			errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.MISSING_ASSETINFO, ErrorConstants.ERRORDOMAIN));
		}
		
		return isValid;	
	}
	
	public static boolean validateBasicAssetInfo(BasicAssetInfo basicAssetInfo, List<CommonErrorData> errorDataList)
	{
		boolean isValid = true;
		
		if (basicAssetInfo != null)
		{
			if (basicAssetInfo.getAssetName() == null && (basicAssetInfo.getAssetKey() == null || basicAssetInfo.getAssetKey().getAssetName() == null))
			{
				isValid = false;
				errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ASSET_NAME_MISSING, ErrorConstants.ERRORDOMAIN));
			}
			else
			{
				if (basicAssetInfo.getAssetName() == null)
				{
					basicAssetInfo.setAssetName(basicAssetInfo.getAssetKey().getAssetName());
				}
			}
			
			if (basicAssetInfo.getVersion() == null)
			{
				isValid = false;
				errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ASSET_VERSION_MISSING, ErrorConstants.ERRORDOMAIN));
			}
			else
			{
				if (!RepositoryServiceValidateUtil.checkVersionFormat(basicAssetInfo.getVersion(), 3))
				{
					isValid = false;
					String version = basicAssetInfo.getVersion();
					errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.INVALID_VERSION, ErrorConstants.ERRORDOMAIN, 
																		new String[]{version}));
				}
			}
			
			if (basicAssetInfo.getAssetType() == null)
			{
				isValid = false;
				errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ASSET_TYPE_MISSING, ErrorConstants.ERRORDOMAIN));
			}
			
			isValid = validateAssetKey(basicAssetInfo.getAssetKey(), errorDataList);
		}
		else
		{
			isValid = false;
			errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.BASE_ASSETINFO_MISSING, ErrorConstants.ERRORDOMAIN));
		}
			
		return isValid;
	}
	
	public static boolean validateArtifactInfos(List<ArtifactInfo> artifactInfos, List<CommonErrorData> errorDataList) 
	{
		boolean isValid = true;
		
		if(artifactInfos == null || artifactInfos.size() <= 0)
		{
			isValid = false;
			errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.NO_ARTIFACTS_FOR_ADDING, ErrorConstants.ERRORDOMAIN));
		}
		else
		{
			for(ArtifactInfo artifactInfo : artifactInfos)
			{
				if(artifactInfo.getArtifact()!= null)
				{
					if(artifactInfo.getArtifact().getArtifactName()== null)
					{
						isValid = false;
						errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ARTIFACT_NAME_MISSING, ErrorConstants.ERRORDOMAIN));
					}
					if(artifactInfo.getArtifact().getArtifactValueType()==null)
					{
						isValid = false;
						errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ARTIFACT_VALUE_TYPE_MISSING, ErrorConstants.ERRORDOMAIN));
					}
					if(artifactInfo.getArtifact().getArtifactCategory()==null)
					{
						isValid = false;
						errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ARTIFACT_CATEGORY_MISSING, ErrorConstants.ERRORDOMAIN));
					}
				}
				else
				{
					isValid = false;
					errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ARTIFACT_MISSING, ErrorConstants.ERRORDOMAIN));
				}
				
				if(artifactInfo.getArtifactDetail()==null)
				{
					isValid = false;
					errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ARTIFACT_EMPTY_EXCEPTION, ErrorConstants.ERRORDOMAIN));
				}
			}
		}
		
		return isValid;
	}
	
	public static boolean validateFlattenedRelationship(FlattenedRelationship flattenedRelationship, List<CommonErrorData> errorDataList)
	{
		boolean isValid = true;
		
		if(flattenedRelationship != null) 
		{
			AssetKey sourceAsset = flattenedRelationship.getSourceAsset();
			if(sourceAsset != null) 
			{
				isValid = RepositoryServiceOperationValidationUtil.validateAssetKey(sourceAsset, errorDataList);
			}
			List<Relation> relatedAssets = flattenedRelationship.getRelatedAsset();
			if(relatedAssets != null) 
			{
				for (Relation relation : relatedAssets) {						
					if( relation.getSourceAsset() != null)
					{
						isValid = RepositoryServiceOperationValidationUtil.validateAssetKey(relation.getSourceAsset(), errorDataList);
					}
					if(relation.getAssetRelationship() == null)
					{
						isValid = false;
						errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.RELATIONSHIP_NAME_NOT_ENTERED, ErrorConstants.ERRORDOMAIN));
					}
					AssetKey targetAsset = relation.getTargetAsset();
					if(targetAsset != null)
					{
						isValid = RepositoryServiceOperationValidationUtil.validateAssetKey(targetAsset, errorDataList);
					}
					else
					{
						isValid = false;
						errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.TARGET_ASSETKEY_MISSING, ErrorConstants.ERRORDOMAIN));
					}
				}
			}
		}
		else
		{
			isValid = false;
			errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.FLATTENED_RELATIONSHIP_MISSING, ErrorConstants.ERRORDOMAIN));
		}
		
		return isValid;
	}
	
	public static boolean validateAssetKey(AssetKey assetKey, List<CommonErrorData> errorDataList)
	{
		boolean isValid = true;
		
		if(assetKey != null) 
		{
			if(assetKey.getAssetName() == null && assetKey.getAssetId() == null) 
			{
				errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ASSET_NAME_AND_ID_MISSING, ErrorConstants.ERRORDOMAIN));
				isValid = false;
			}
			Library sourceLibrary = assetKey.getLibrary();
			if(sourceLibrary == null)
			{
				errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.LIBRARY_MISSING, ErrorConstants.ERRORDOMAIN));
				isValid = false;
			}
			if(sourceLibrary.getLibraryName() == null)
			{
				errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.LIBRARY_NAME_MISSING, ErrorConstants.ERRORDOMAIN));
				isValid = false;
			}
		}
		else
		{
			errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.ASSETKEY_MISSING, ErrorConstants.ERRORDOMAIN));
			isValid = false;
		}
		
		return isValid;
	}
	public static boolean validateFlattenedRelationshipForUpdate(FlattenedRelationshipForUpdate flattenedRelationshipForUpdate, List<CommonErrorData> errorDataList)
	{
		boolean isValid = true;
		if(flattenedRelationshipForUpdate != null)
		{
			AssetKey sourceAssetKey = flattenedRelationshipForUpdate.getSourceAsset();
			if(sourceAssetKey != null)
			{
				isValid = validateAssetKey(sourceAssetKey, errorDataList);
			}
			List<RelationForUpdate> relationForUpdateList = flattenedRelationshipForUpdate.getRelatedAsset();
			if(relationForUpdateList.size() > 0)
			{
				for (RelationForUpdate relationForUpdate : relationForUpdateList) 
				{
					AssetKey currentSourceAssetKey = relationForUpdate.getCurrentSourceAsset();
					if(currentSourceAssetKey != null)
					{
						isValid = validateAssetKey(currentSourceAssetKey, errorDataList);
					}
					
					AssetKey currentTargetAssetKey = relationForUpdate.getCurrentTargetAsset();
					if(currentTargetAssetKey != null)
					{
						isValid = validateAssetKey(currentTargetAssetKey, errorDataList);
					}
					Relation relation = relationForUpdate.getNewRelation();
					if(relation != null)
					{
						AssetKey newSourceAssetKey = relation.getSourceAsset();
						if(newSourceAssetKey != null) 
						{
							isValid = validateAssetKey(newSourceAssetKey, errorDataList);
						}
						
						AssetKey newTargetAssetKey = relation.getTargetAsset();
						if(newTargetAssetKey != null)
						{
							isValid = validateAssetKey(newTargetAssetKey, errorDataList);
						}
						else
						{
							errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.TARGET_ASSETKEY_MISSING, ErrorConstants.ERRORDOMAIN));
							isValid = false;
						}
						if(relation.getAssetRelationship() == null)
						{
							errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.RELATIONSHIP_NAME_NOT_ENTERED, ErrorConstants.ERRORDOMAIN));
							isValid = false;
						}
					}
					else
					{
						if(!relationForUpdate.isDeleteRelation())
						{
							errorDataList.add(ErrorDataFactory.createErrorData(ErrorConstants.NEW_RELATION_MISSING, ErrorConstants.ERRORDOMAIN));
							isValid = false;
						}
					}
				}
			}
		}
		else
		{
			isValid = false;
		}
		return isValid;
	}
}
