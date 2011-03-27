package org.ebayopensource.turmeric.demo.consumer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.ebayopensource.turmeric.runtime.binding.objectnode.impl.JavaObjectNodeImpl;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.search.v1.types.ItemType;

import com.amazon.soap.amazonsearchservice.gen.SharedAmazonSearchServiceV1Consumer;
import com.amazon.webservices.awsecommerceservice._2010_12_01.Item;
import com.amazon.webservices.awsecommerceservice._2010_12_01.ItemSearch;
import com.amazon.webservices.awsecommerceservice._2010_12_01.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2010_12_01.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2010_12_01.Items;
import com.amazon.webservices.awsecommerceservice._2010_12_01.OfferSummary;
import com.amazon.webservices.awsecommerceservice._2010_12_01.Price;
import com.ebay.marketplace.search.v1.services.Amount;
import com.ebay.marketplace.search.v1.services.FindItemsByKeywordsRequest;
import com.ebay.marketplace.search.v1.services.FindItemsByKeywordsResponse;
import com.ebay.marketplace.search.v1.services.SearchItem;
import com.ebay.marketplace.search.v1.services.findingservice.gen.SharedFindingServiceV1Consumer;

public class ItemDataSources {

	
	public static final List<ItemType> getItemsFromEbay(List<String> keywords) {
		try {
			SharedFindingServiceV1Consumer consumer = new SharedFindingServiceV1Consumer("ItemSearchConsumers");
			consumer.getService().setSessionTransportHeader("X-EBAY-SOA-SECURITY-APPNAME", "Testfrea-7731-4014-bbb8-338366a2c815");
			consumer.getService().setSessionTransportHeader("X-EBAY-SOA-SERVICE-NAME", "FindingService");
			consumer.getService().setSessionTransportHeader("X-EBAY-SOA-OPERATION-NAME", "findItemsByKeywords");
			FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();
			for (String keyword : keywords)
				request.setKeywords(keyword);
			FindItemsByKeywordsResponse response = consumer.findItemsByKeywords(request);
			ArrayList<ItemType> itemListings = new ArrayList<ItemType>();
			if (response.getSearchResult() == null) {
				return itemListings;
			}
			List<SearchItem> results = response.getSearchResult().getItem();
			for (SearchItem sItem : results) {
				ItemType itemListing = new ItemType();
				itemListing.setId(sItem.getItemId());
				itemListing.setName(sItem.getTitle());
				itemListing.setType(sItem.getListingInfo().getListingType());
				itemListing.setCondition((sItem.getCondition() == null)? "" : sItem.getCondition().getConditionDisplayName());
				Amount amount = sItem.getListingInfo().getBuyItNowPrice();
				if (amount != null && amount.getValue() > 0.01) 
					itemListing.setPrice(amount.getValue());
				else {
					amount = sItem.getSellingStatus().getCurrentPrice();
					if (amount != null && amount.getValue() > 0.01) {
						itemListing.setPrice(amount.getValue());
					} 
				}
				itemListings.add(itemListing);
				itemListing.setSource("eBay ");
			}
			return itemListings;
		} catch (ServiceException se) {
			System.err.print("Exception calling eBay FindingService: " + se );
		} catch (NumberFormatException nfe) {
			System.err.print("Exception convert string to int: " + nfe);
		}
		return null;
	}
	
	private static final String AMAZON_ACCESS_KEY = "AKIAJOK2WVETP65PUAUQ";
	private static final String AMAZON_SECRETE_KEY = "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";

	public static final List<ItemType> getItemsFromAmazon(List<String> keywords) {
		try {
			SharedAmazonSearchServiceV1Consumer consumer = new SharedAmazonSearchServiceV1Consumer("ItemSearchConsumers");
			ItemSearch itemSearch = new ItemSearch();
			for (String keyword : keywords) {
				ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
				itemSearchRequest.setSearchIndex("All");
				itemSearchRequest.setKeywords(keyword);
				itemSearch.getRequest().add(itemSearchRequest);
				itemSearchRequest.setCondition("All");
				itemSearchRequest.getResponseGroup().add("OfferSummary,ItemAttributes,ItemIds");
				itemSearchRequest.getResponseGroup().add("ItemAttributes");
			}
			itemSearch.setAWSAccessKeyId(AMAZON_ACCESS_KEY);
			QName headerNS = new QName("http://security.amazonaws.com/doc/2007-01-01/");
			SignedRequestHelper helper = null;
			try {
				helper = new SignedRequestHelper("ItemSearch", AMAZON_SECRETE_KEY);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			String timestamp = helper.getTimestamp();
			String signature = helper.getSignature(timestamp);
			System.out.println("AWSAccessKeyId="+AMAZON_ACCESS_KEY);
			System.out.println("signature="+signature);
			System.out.println("timestamp="+timestamp);
			
			JavaObjectNodeImpl soapHeaderSignatureParam = new JavaObjectNodeImpl(new QName("http://security.amazonaws.com/doc/2007-01-01/", "Signature"), signature);
			consumer.getService().getRequestContext().addMessageHeader(soapHeaderSignatureParam);
			
			JavaObjectNodeImpl soapHeaderAWSAccessKeyIdParam = new JavaObjectNodeImpl(new QName("http://security.amazonaws.com/doc/2007-01-01/","AWSAccessKeyId"),AMAZON_ACCESS_KEY);
			consumer.getService().getRequestContext().addMessageHeader(soapHeaderAWSAccessKeyIdParam);
			
			JavaObjectNodeImpl soapHeaderTimestampParam = new JavaObjectNodeImpl(new QName("http://security.amazonaws.com/doc/2007-01-01/", "Timestamp"),helper.getTimestamp());
			consumer.getService().getRequestContext().addMessageHeader(soapHeaderTimestampParam);
			consumer.getService().getInvokerOptions().setMessageProtocolName("SOAP11");
			consumer.getService().getRequestContext().setTransportHeader("SOAPAction", "http://soap.amazon.com/ItemSearch");
			ItemSearchResponse response = consumer.itemSearch(itemSearch);
			
			List<Items> itemsList = response.getItems();
			ArrayList<ItemType> itemListings = new ArrayList<ItemType>();
			for (Items items : itemsList) {
				for (Item item : items.getItem()) {
					ItemType itemListing = new ItemType();
					itemListing.setId(item.getASIN());
					itemListing.setName(item.getItemAttributes().getTitle());
					itemListing.setType("Fixed Price");
					itemListing.setCondition("New");
					OfferSummary os = item.getOfferSummary();
					boolean isParentItem = os == null || ((Integer.valueOf(os.getTotalCollectible()) 
							+ Integer.valueOf(os.getTotalNew()) 
							+ Integer.valueOf(os.getTotalRefurbished()) 
							+ Integer.valueOf(os.getTotalUsed())) == 0);
					if (isParentItem) {
						continue;
					}
					Price p = item.getOfferSummary().getLowestNewPrice();
					if (p == null || p.getAmount() == null) {
						p = item.getOfferSummary().getLowestUsedPrice();
					}
					if (p != null && p.getAmount() != null) {
						itemListing.setPrice((double)p.getAmount().doubleValue()/100.0);
					}
					itemListings.add(itemListing);
					itemListing.setSource("Amazon");
				}
			}
			
			return itemListings;
		} catch (ServiceException se) {
			System.err.print("Exception calling eBay FindingService: " + se );
		} catch (NumberFormatException nfe) {
			System.err.print("Exception convert string to int: " + nfe);
		}
		return null;
	}

}
