package com.eltaieb;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UrlDynamicPartsAllocatorApplication {

	private static final String DYNAMIC_VALUE = "{DYNAMIC_VALUE}";

	public static void main(String[] args) {
		SpringApplication.run(UrlDynamicPartsAllocatorApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return (args) -> {

			try (Stream<String> stream = Files
					.lines(Paths.get(ClassLoader.getSystemResource("./urls.txt").toURI()), StandardCharsets.UTF_8)
					.filter(line -> line.trim().length() != 0)) {

				List<String> urls = stream.collect(Collectors.toList());
				processUrls(urls);
			} catch (Exception ex) {
				// handle what is happened in the file doesn't exist
			}
		};
	}

	private void processUrls(List<String> urls) {
		int arrSize = urls.size();
		if (arrSize == 0) {
			System.out.println("file is empty");
			return;
		} else if (arrSize == 1) {
			System.out.println("file have only one url so cannot be processed ");
			return;
		} else {
			sortUrls(urls);
			Set<String> linkedHashSet = new LinkedHashSet<>();
			String association[][] = construct2DArray(urls);
 
			for (int r = 0; r < association.length-1; r++) {

				int baseRow =r;
				int compareRow =r+1;
				
				int currentRowLength = association[baseRow].length;
				int nextRowLength    = association[compareRow].length;

				if (nextRowLength < currentRowLength) {
					continue;
				}

				for (int c = 1; c < association[baseRow].length; c++) {

					String current = association[baseRow][c];
					String next = association[compareRow][c];
					if (!current.equals(next)) {
						association[baseRow][c] = DYNAMIC_VALUE;
					}

				}
				String currentUrl =String.join("/", association[baseRow]);
				if(currentUrl.contains(DYNAMIC_VALUE))
				{
					linkedHashSet.add(currentUrl);
				}
			}

			linkedHashSet.forEach(System.out::println);
		}

	}

	private String[][] construct2DArray(List<String> urls) {
		String[][] association = new String[urls.size()][];
		for (int r = 0; r < urls.size(); r++) {
			String currentRow = urls.get(r);
			String[] currentRowStrings = currentRow.split("/");
			association[r] = new String[currentRowStrings.length];
			for (int c = 0; c < currentRowStrings.length; c++) {
				association[r][c] = currentRowStrings[c];
			}
		}
		return association;
	}
	

	private void sortUrls(List<String> urls) {
		Collections.sort(urls, (a, b) -> {
			String[] first = a.split("/");
			String[] second = b.split("/");
			if (first.length > 1 && second.length > 1) {
				// if the two urls have same base .. sort them as per length
				// for so results will be like the following
				// /users/
				// /users/Marcelo/info/
				// /users/Maria/info/location
				if (first[1].equals(second[1])) {
					return new Integer(a.length()).compareTo(b.length());
				}
			}
			return a.compareTo(b);
		});

	}
}
