package com.patricio.peticio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main extends ApplicationAdapter {
	Dialog dlg;
	Skin skin;
	Stage stage;
	TextButton btn;
	OrthographicCamera camera;
	public final int GAME_WIDTH = 1920;
	public final int GAME_HEIGHT = 1080;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);

		skin = new Skin(Gdx.files.internal("uiskin.json"));
		stage = new Stage();

		dlg = new Dialog("", skin);
		Label titleLabel = new Label("Press to get info of the \ntwo Rick & Morty characters!!", skin);
		titleLabel.setSize(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.1f);
		titleLabel.setWrap(false); // Wrap the text
		dlg.text(titleLabel);
		dlg.getContentTable().defaults().width(Gdx.graphics.getWidth() * 0.6f).pad(20); // Center the text
		btn = new TextButton("Press", skin);

		dlg.button(btn);
		dlg.show(stage);

		// Adjust dialog size and position
		float dialogWidth = Gdx.graphics.getWidth() * 0.7f;
		float dialogHeight = Gdx.graphics.getHeight() * 0.5f;
		dlg.setSize(dialogWidth, dialogHeight);
		dlg.setPosition((Gdx.graphics.getWidth() - dialogWidth) / 2f, (Gdx.graphics.getHeight() - dialogHeight) / 2f);

		// Increase font size for button text
		TextButton.TextButtonStyle buttonStyle = btn.getStyle();
		buttonStyle.font.getData().setScale(2); // Increase font scale for button text

		btn.getLabel().setAlignment(Align.center);

		btn.addListener(event -> {
			HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
			Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("https://rickandmortyapi.com/api/character/1,2").build();
			Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
				@Override
				public void cancelled() {
					System.out.println("Cancelled");
				}

				@Override
				public void failed(Throwable t) {
					System.out.println("Error: " + t.getMessage());
				}

				@Override
				public void handleHttpResponse(Net.HttpResponse httpResponse) {
					byte[] result = httpResponse.getResult();
					String json = convertByteArrayToJson(result);
					System.out.println("Info: " + json);
					updateLabelsFromJson(json);
				}
			});
			return true;
		});

		/*float dialogVerticalCenter = dlg.getY() + dlg.getHeight() / 2f;

		// Calculate vertical offset for the first set of labels with separation
		float label1VerticalOffset = dialogVerticalCenter + 50;
		float label2VerticalOffset = label1VerticalOffset + 50;
		float label3VerticalOffset = label2VerticalOffset + 50;

		// Create labels for the first set
		Label infoLabel1 = new Label("INFORMATION 1", skin);
		infoLabel1.setAlignment(Align.center);
		infoLabel1.setPosition((Gdx.graphics.getWidth() - infoLabel1.getWidth()) / 2f, label1VerticalOffset);

		Label infoLabel2 = new Label("INFORMATION 2", skin);
		infoLabel2.setAlignment(Align.center);
		infoLabel2.setPosition((Gdx.graphics.getWidth() - infoLabel2.getWidth()) / 2f, label2VerticalOffset);

		Label infoLabel3 = new Label("INFORMATION 3", skin);
		infoLabel3.setAlignment(Align.center);
		infoLabel3.setPosition((Gdx.graphics.getWidth() - infoLabel3.getWidth()) / 2f, label3VerticalOffset);

		// Calculate vertical offset for the second set of labels with separation
		float label4VerticalOffset = label3VerticalOffset + 50;
		float label5VerticalOffset = label4VerticalOffset + 50;
		float label6VerticalOffset = label5VerticalOffset + 50;

		// Create labels for the second set
		Label infoLabel4 = new Label("INFORMATION 4", skin);
		infoLabel4.setAlignment(Align.center);
		infoLabel4.setPosition((Gdx.graphics.getWidth() - infoLabel4.getWidth()) / 2f, label4VerticalOffset);

		Label infoLabel5 = new Label("INFORMATION 5", skin);
		infoLabel5.setAlignment(Align.center);
		infoLabel5.setPosition((Gdx.graphics.getWidth() - infoLabel5.getWidth()) / 2f, label5VerticalOffset);

		Label infoLabel6 = new Label("INFORMATION 6", skin);
		infoLabel6.setAlignment(Align.center);
		infoLabel6.setPosition((Gdx.graphics.getWidth() - infoLabel6.getWidth()) / 2f, label6VerticalOffset);

		// Add labels to the stage
		stage.addActor(infoLabel1);
		stage.addActor(infoLabel2);
		stage.addActor(infoLabel3);
		stage.addActor(infoLabel4);
		stage.addActor(infoLabel5);
		stage.addActor(infoLabel6);
	*/
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public String convertByteArrayToJson(byte[] byteArray) {
		String jsonString = new String(byteArray, StandardCharsets.UTF_8);
		return jsonString;
	}

	private void updateLabelsFromJson(String json) {
		JsonValue jsonValue = new JsonReader().parse(json);

		if (jsonValue != null && jsonValue.isArray()) {
			float verticalOffset = dlg.getY() + dlg.getHeight() - 40;

			for (JsonValue character : jsonValue) {
				String name = character.getString("name", "");
				String species = character.getString("species", "");
				String originName = character.get("origin").getString("name", "");

				// Create labels for each character
				Label nameLabel = new Label("Name: " + name, skin);
				nameLabel.setAlignment(Align.center);
				nameLabel.setPosition((Gdx.graphics.getWidth() - nameLabel.getWidth()) / 2f, verticalOffset);
				verticalOffset -= nameLabel.getHeight() + 40; // Adding 40 to verticalOffset for additional padding

				Label speciesLabel = new Label("Species: " + species, skin);
				speciesLabel.setAlignment(Align.center);
				speciesLabel.setPosition((Gdx.graphics.getWidth() - speciesLabel.getWidth()) / 2f, verticalOffset);
				verticalOffset -= speciesLabel.getHeight() + 40; // Adding 40 to verticalOffset for additional padding

				Label originLabel = new Label("Origin: " + originName, skin);
				originLabel.setAlignment(Align.center);
				originLabel.setPosition((Gdx.graphics.getWidth() - originLabel.getWidth()) / 2f, verticalOffset);
				verticalOffset -= originLabel.getHeight() + 40; // Adding 40 to verticalOffset for additional padding

				// Add labels to the stage
				stage.addActor(nameLabel);
				stage.addActor(speciesLabel);
				stage.addActor(originLabel);
			}
		}
	}

}
