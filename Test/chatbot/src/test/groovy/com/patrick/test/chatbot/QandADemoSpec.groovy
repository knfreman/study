package com.patrick.test.chatbot

import java.util.List

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.json.JSONArray
import org.json.JSONObject

import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * 
 * @author Patrick Pan
 *
 */
class QandADemoSpec extends Specification{
	
	// response list
	static final EXCEL_NAME='QandADemoForStringSpec.xls'
	static final QandAXlsReader READER=QandAXlsReader.STRING
	// response key
//	static final EXCEL_NAME='QandADemoForKeySpec.xls'
//	static final QandAXlsReader READER=QandAXlsReader.KEY
	
	static final URL='http://192.168.16.12:3004/api/message'
	static final LANG='EN'
	static final CHANNEL='web'

	@Shared List sessionIds=[]
	@Shared List questions=[]
	@Shared List answers=[]
	
	def setupSpec() {
		List questionsAndAnswersList=READER.read(EXCEL_NAME)
		
		questionsAndAnswersList.each{questionsAndAnswers->
			def sessionId="sessionId_"+(new Random()).nextInt()
			
			questionsAndAnswers.each{questionAndAnswers->
				questionAndAnswers.each{question,answer->
					sessionIds<<sessionId
					questions<<question
					answers<<answer
				}
			}
		}
	}
	
	@Unroll
	def "When sessionId is [#sessionId], send [#message] to SOE and one of #expectedResponseList should be returned."(){
		setup:'prepare before test'
			JSONObject json=new JSONObject()
			json.put('sessionid', sessionId)
			json.put('message', message)
			json.put('lang', LANG)
			json.put('channel', CHANNEL)
			
			HttpClient httpclient = new DefaultHttpClient()
			HttpPost post = new HttpPost(URL)
			post.setEntity(new StringEntity(json.toString(),ContentType.APPLICATION_JSON))
		when:'send message to server'
			HttpResponse response=httpclient.execute(post)
			JSONArray list=parseResponseAndGetList(response)
		then:'response should be returned successfully'
			isResponseValid(expectedResponseList,list)
		where:
			sessionId<<sessionIds
			message<<questions
			expectedResponseList<<answers
	}
	
	def parseResponseAndGetList(HttpResponse response) {
		String responseString=EntityUtils.toString(response.getEntity())
		println(responseString)
		JSONObject responseJson=new JSONObject(responseString)
		
		switch(READER) {
			case QandAXlsReader.STRING:
				return responseJson.getJSONObject('data').getJSONArray('list')
			case QandAXlsReader.KEY:
				return responseJson.getJSONObject('data').getJSONArray('respkeylist')
		}
	}
	
	def isResponseValid(List expectedResponseList,JSONArray list) {
		List unexpectedResponseList=[]
		list.length().times{
			if(!expectedResponseList.contains(list.get(it))){
				unexpectedResponseList<<list.get(it)
			}
		}

		if(unexpectedResponseList.isEmpty()){
			return true
		}

		println("Unexpected Response: "+unexpectedResponseList)
		return false
	}
}
