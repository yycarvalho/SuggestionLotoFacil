package com.lotofacil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class LotoFacil {

	private static HashMap<Integer, Integer> balanceNumbers = new HashMap<>();
	private static ArrayList<Integer> gameSuggestion = new ArrayList<>();
	
	
	public static void main(String[] args) throws Exception {
		
		//=======================================
		int quantityOfContestsToSearch = 20;
		int contest = 2743;
		//=======================================
		
		contest-=quantityOfContestsToSearch*2;
		boolean compareNextGames = true;
		
		System.out.println("Starting Count!");
		JSONTokener tokener = null;
		for (int i = 0; i < quantityOfContestsToSearch; i++) {
			BufferedReader leitor = ConsultAPI(contest+i);
			try {
				tokener = new JSONTokener(leitor.readLine().toString());
			} catch (Exception e) {
				continue;
			}
			String extractValue = extractValue(tokener, "dezenas");
			String[] treatedValues = TreatedValues(extractValue);
			Balance(treatedValues);
			System.out.println("Contest: " + (contest+i));
		}
		
		Result(quantityOfContestsToSearch);
		
		if(compareNextGames)
		{
			float betValue = (float) (2.50*quantityOfContestsToSearch);
			contest+=quantityOfContestsToSearch+1;
			for (int i = 0; i < quantityOfContestsToSearch; i++) {
				BufferedReader leitor = ConsultAPI(contest+i);
				try {
					tokener = new JSONTokener(leitor.readLine().toString());
				} catch (Exception e) {
					continue;
				}
				String extractValue = extractValue(tokener, "dezenas");
				String[] treatedValues = TreatedValues(extractValue);
				float pointsNextGames = PointsNextGames(contest+i, treatedValues);
				betValue += pointsNextGames;
			}
		System.out.println("\nInvestments: R$" + (2.50*quantityOfContestsToSearch) + "\nReturns: R$" + betValue +"\n" + (betValue < (2.50*quantityOfContestsToSearch) ? "Bed Investments" : "Good Investments"));
		}
		
	}

	private static float PointsNextGames(int contest, String[] treatedValues) {
		int points = 0;
		for (int x = 0; x < treatedValues.length; x++) {
			Integer numberLoteria = Integer.valueOf(treatedValues[x]);
			if(gameSuggestion.contains(numberLoteria)) {
				points++;
			}
		}
		System.out.println("Contest: " + (contest) + ": "+ points + " points" + (points >= 11 ? " - It won" : ""));
		return (float) (points < 11 ? -2.50 : points == 11 ? 5.00 : points == 12 ? 10.00 : points == 13 ? 25.00 : points == 14 ? 1654 : 1941323.00);
	}

	private static BufferedReader ConsultAPI(int contest)
			throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL("https://loteriascaixa-api.herokuapp.com/api/lotofacil/"+(contest));
		HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
		conexao.setRequestMethod("GET");
		BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
		return leitor;
	}

	private static void Result(int quantityOfContestsToSearch) {
		System.out.println("List of the most drawn numbers of the last " + quantityOfContestsToSearch + " contest");
		balanceNumbers.entrySet().stream().sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())).limit(15).forEach(x -> {
			System.out.println(x.getKey() + " he appeared: " + x.getValue() + "x");
			gameSuggestion.add(x.getKey());
		});
		StringBuilder gameSuggestionString = new StringBuilder();
		gameSuggestionString.append("Game Suggestion: ");
		gameSuggestion.stream().sorted((a, b) -> Integer.compare(a, b)).forEach(x-> gameSuggestionString.append(x + " "));
		System.out.println(gameSuggestionString.toString());
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

