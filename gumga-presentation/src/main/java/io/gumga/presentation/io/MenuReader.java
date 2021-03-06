package io.gumga.presentation.io;

import com.google.common.io.CharStreams;
import io.gumga.presentation.menu.Menu;
import io.gumga.presentation.menu.MenuComponent;
import io.gumga.presentation.menu.MenuGrupo;
import io.gumga.presentation.menu.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe para acesso e manipulação de menu e itens de menu
 */
public class MenuReader {
    
       private static final Logger log = LoggerFactory.getLogger(MenuReader.class);

	
	private MenuReader() {
	}

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	public static Menu loadMenu() throws IOException {
		return digesterMenu(loadMenuFile());
	}

	/**
	 * Carrega as definições de menu de acordo com o arquivo de configurações
	 * @return Uma lista contendo as linhas lidas
	 * @throws IOException
	 */
	private static List<String> loadMenuFile() throws IOException {
		InputStream menuStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("menu.config");

		if (menuStream == null) {
			log.warn("menu.config não encontrado, o menu não será carregado.");
			return Collections.emptyList();
		}

		return CharStreams.readLines(new InputStreamReader(menuStream, "UTF-8"));
	}

	/**
	 * Recebe uma lista de String e gera um Menu com cada um dos ítens
	 * {@link Menu}
	 * @param menuOptions Lista com as opções de menu
	 * @return Objeto Menu carregado com as opções de entrada
	 */
	public static Menu digesterMenu(List<String> menuOptions) {
		Menu menu = new Menu();

		for (String menuOption : menuOptions) {
			MenuComponent item = createMenuItem(menuOption);
			menu.addItem(item, item.getLevel());
		}

		return menu;
	}


	/**
	 * Gera um item de menu a partir de uma String
	 * @param menuOption String contendo a opção de menu
	 * @return Objeto MenuComponent contendo um ítem de menu
	 */
	private static MenuComponent createMenuItem(String menuOption) {
		Map<String, String> properties = getMenuItemProperties(menuOption);

		String replaced = menuOption.replace("\t", "");
		int level = menuOption.length() - replaced.length();

		String label = replaced.contains("{") ? replaced.substring(0,
				replaced.indexOf("{") - 1) : replaced;
		String url = properties.get("url");
		String id = properties.get("id");
		String clazz = properties.get("class");

		MenuComponent item = (url != null) ? new MenuItem(label, url)
				: new MenuGrupo(label);

		item.setLevel(level);
		if (id != null)
			item.setId(id);
		if (clazz != null)
			item.setClazz(clazz);

		return item;
	}

	/**
	 * Cria uma coleção de String contendo as propriedades de um Item de Menu
	 * @param menuOption String contendo o menu
	 * @return Uma coleção de String
	 */
	private static Map<String, String> getMenuItemProperties(String menuOption) {
		Pattern pattern = Pattern.compile("\\{(.*?)\\}");
		Matcher matchPattern = pattern.matcher(menuOption);

		Map<String, String> properties = new HashMap<>();

		while (matchPattern.find()) {
			String propertiesInline = matchPattern.group(1).trim();
			String[] propertiesSplited = propertiesInline.split(" ");

			for (String property : propertiesSplited) {
				String[] splited = property.split("=");
				properties.put(splited[0], splited[1].replaceAll("\"", ""));
			}
		}

		return properties;
	}

}
