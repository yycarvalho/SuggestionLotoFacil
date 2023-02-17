package com.lotofacil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class LotoFacil {

	private static HashMap<Integer, Integer> balanceNumbers = new HashMap<>();
	
	public static void main(String[] args) throws Exception {

		int contest = 2742;
		int quantityOfContestsToSearch = 10;
		System.out.println("Starting Count!");
		for (int i = 0; i < quantityOfContestsToSearch; i++) {
			URL url = new URL("https://loteriascaixa-api.herokuapp.com/api/lotofacil/"+(contest-i));
			HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
			conexao.setRequestMethod("GET");
			BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			JSONTokener tokener = new JSONTokener(leitor.readLine().toString());
			String extractValue = extractValue(tokener, "dezenas");
			String[] treatedValues = TreatedValues(extractValue);
			Balance(treatedValues);
			System.out.println("Contest: " + (contest-i));
		}
		Result(quantityOfContestsToSearch);
		
	}

	private static void Result(int quantityOfContestsToSearch) {
		System.out.println("List of the most drawn numbers of the last " + quantityOfContestsToSearch + " contest");
		StringBuilder gameSuggestion = new StringBuilder();
		gameSuggestion.append("Game Suggestion: ");
		balanceNumbers.entrySet().stream().sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())).limit(15).forEach(x -> {
			System.out.println(x.getKey() + " he appeared: " + x.getValue() + "x");
			gameSuggestion.append(x.getKey() +" ");
		});
		System.out.println(gameSuggestion.toString());
	}

	private static void Balance(String[] treatedValues) {
		for (int x = 0; x < treatedValues.length; x++) {
			Integer numberLoteria = Integer.valueOf(treatedValues[x]);
			if(balanceNumbers.containsKey(numberLoteria))
				balanceNumbers.put(numberLoteria, balanceNumbers.get(numberLoteria) + 1);
			else
				balanceNumbers.put(numberLoteria, 1);
		}
	}

	private static String[] TreatedValues(String extractValue) {
		String treatedValues = extractValue.replace("[", "").replace("\"", "").replace("]", "");

		String[] split = treatedValues.split(",");
		return split;
	}

	private static String extractValue(JSONTokener tokener, String string) throws JSONException {
		JSONObject jsonObject = new JSONObject(tokener);
		String dezenas = jsonObject.optString(string);
		return dezenas;
	}

}

