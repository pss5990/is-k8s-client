package com.intellisense.quiz;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentCondition;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.util.Config;

public class K8STest {
	private static final String HEADING_SEPARATOR = "\t\t\t";
	private static final String DATA_SEPARATOR = "                                ";

	public static void main(String[] args) throws IOException, ApiException {
		ApiClient client = null;
		try {
			client = Config.defaultClient();
		}catch(Exception e) {
			throw new RuntimeException("Exception while connecting to K8S cluster !");
		}
		
		AppsV1Api appsApi = new AppsV1Api(client);
		
		table();
		
		V1DeploymentList deployList = appsApi.listDeploymentForAllNamespaces(null, null, null, null, null, null, null, null, null);
		deployList.getItems().stream().forEach(d -> printDetails(d));
	}


	
	private static void printDetails(V1Deployment deployment) {
		String deployName = deployment.getMetadata().getName();
		System.out.print(deployName);
		System.out.print(DATA_SEPARATOR.substring(deployName.length()));
		System.out.print(deployment.getSpec().getTemplate().getSpec()
				.getContainers().stream()
				.map(c -> c.getImage())
				.collect(Collectors.toList()));
		List<V1DeploymentCondition> conditions = deployment.getStatus().getConditions();
		System.out.print(DATA_SEPARATOR);
		System.out.print(conditions.get(conditions.size()-1).getLastUpdateTime());
		System.out.println();
	}
	
	private static void table() {
		System.out.println("---------------------------------------------------------------------------------------------------------");
		System.out.println("Deployment Name"+HEADING_SEPARATOR+"Image"+HEADING_SEPARATOR+"Deployment Update Date"+HEADING_SEPARATOR);
		System.out.println("---------------------------------------------------------------------------------------------------------");
	}
	
	
}
