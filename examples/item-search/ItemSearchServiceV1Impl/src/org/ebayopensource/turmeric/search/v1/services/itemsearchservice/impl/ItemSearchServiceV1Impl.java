
package org.ebayopensource.turmeric.search.v1.services.itemsearchservice.impl;

import java.util.List;

import org.ebayopensource.turmeric.common.v1.types.CommonErrorData;
import org.ebayopensource.turmeric.common.v1.types.ErrorMessage;
import org.ebayopensource.turmeric.demo.consumer.ItemDataSources;
import org.ebayopensource.turmeric.errorlibrary.search.ErrorConstants;
import org.ebayopensource.turmeric.runtime.common.exceptions.ErrorDataFactory;
import org.ebayopensource.turmeric.runtime.common.pipeline.MessageContextAccessor;
import org.ebayopensource.turmeric.search.v1.services.FindByKeywordsRequest;
import org.ebayopensource.turmeric.search.v1.services.FindByKeywordsResponse;
import org.ebayopensource.turmeric.search.v1.services.GetVersionRequest;
import org.ebayopensource.turmeric.search.v1.services.GetVersionResponse;
import org.ebayopensource.turmeric.search.v1.services.itemsearchservice.ItemSearchServiceV1;
import org.ebayopensource.turmeric.search.v1.types.ItemType;

public class ItemSearchServiceV1Impl
    implements ItemSearchServiceV1
{


    public GetVersionResponse getVersion(GetVersionRequest param0) {
    	GetVersionResponse response = new GetVersionResponse();
    	response.setVersion(MessageContextAccessor.getContext().getServiceVersion());
        return response;
    }

	@Override
	public FindByKeywordsResponse findByKeywords(FindByKeywordsRequest findByKeywordsRequest) {
		List<String> keywords = findByKeywordsRequest.getKeyword();
		FindByKeywordsResponse response = new FindByKeywordsResponse();
		List<ItemType> eBayItems = ItemDataSources.getItemsFromEbay(keywords);
		List<ItemType> amazonItems = ItemDataSources.getItemsFromAmazon(keywords);
		System.out.println("##### Number of items from Amazon: " + amazonItems  != null ? amazonItems.size() : 0);
		if (eBayItems.isEmpty() && (amazonItems.isEmpty())) {
			CommonErrorData errorData = ErrorDataFactory.createErrorData(ErrorConstants.NOITEMFOUND,ErrorConstants.ERRORDOMAIN);
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.getError().add(errorData);
			response.setErrorMessage(errMsg);
			return response;
		}
		List<ItemType> itemListing = response.getItem();
		itemListing.addAll(eBayItems);
		itemListing.addAll(amazonItems);
		return response;
	}
}
