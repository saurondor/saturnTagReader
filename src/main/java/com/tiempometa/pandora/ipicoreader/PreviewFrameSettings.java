/**
 * 
 */
package com.tiempometa.pandora.ipicoreader;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author gtasi
 *
 */
public class PreviewFrameSettings {

	private static final Logger logger = Logger.getLogger(PreviewFrameSettings.class);
	public static final String FIELD_NAME_LOCATION = "previewFrameFieldNameLocation";
	public static final String FIELD_SUBEVENT_LOCATION = "previewFrameFieldSubeventLocation";
	public static final String FIELD_CATEGORY_LOCATION = "previewFrameFieldCategoryLocation";
	public static final String FIELD_AGE_LOCATION = "previewFrameFieldAgeLocation";
	public static final String FIELD_GENDER_LOCATION = "previewFrameFieldGenderLocation";
	public static final String FIELD_BIRTHDATE_LOCATION = "previewFrameFieldBirthdateLocation";
	public static final String FIELD_LABEL_COLOR_LOCATION = "previewFrameFieldLabelColorLocation";
	public static final String FIELD_TEAM_LOCATION = "previewFrameFieldTeamLocation";
	public static final String FIELD_PROCEDENCE_LOCATION = "previewFrameFieldProcedenceLocation";
	public static final String FIELD_TOP_BANNER = "previewFrameFieldTopBanner";
	public static final String FIELD_LEFT_BANNER = "previewFrameFieldLeftBanner";
	public static final String FIELD_BOTTOM_BANNER = "previewFrameFieldBottomBanner";
	public static final String FIELD_RIGHT_BANNER = "previewFrameFieldRightBanner";

	private Integer fieldNameLocation = 0;
	private Integer fieldSubeventLocation = 0;
	private Integer fieldCategoryLocation = 0;
	private Integer fieldAgeLocation = 0;
	private Integer fieldGenderLocation = 0;
	private Integer fieldBirthdateLocation = 0;
	private Integer fieldLabelColorLocation = 0;
	private Integer fieldTeamLocation = 0;
	private Integer fieldProcedenceLocation = 0;
	private String fieldTopBanner = null;
	private String fieldBottomBanner = null;
	private String fieldLeftBanner = null;
	private String fieldRightBanner = null;

	public void loadSettings() {
		logger.info("Loading settings");
		fieldNameLocation = loadIntegerField(PreviewFrameSettings.FIELD_NAME_LOCATION);
		fieldSubeventLocation = loadIntegerField(PreviewFrameSettings.FIELD_SUBEVENT_LOCATION);
		fieldCategoryLocation = loadIntegerField(PreviewFrameSettings.FIELD_CATEGORY_LOCATION);
		fieldAgeLocation = loadIntegerField(PreviewFrameSettings.FIELD_AGE_LOCATION);
		fieldGenderLocation = loadIntegerField(PreviewFrameSettings.FIELD_GENDER_LOCATION);
		fieldBirthdateLocation = loadIntegerField(PreviewFrameSettings.FIELD_BIRTHDATE_LOCATION);
		fieldLabelColorLocation = loadIntegerField(PreviewFrameSettings.FIELD_LABEL_COLOR_LOCATION);
		fieldTeamLocation = loadIntegerField(PreviewFrameSettings.FIELD_TEAM_LOCATION);
		fieldProcedenceLocation = loadIntegerField(PreviewFrameSettings.FIELD_PROCEDENCE_LOCATION);
		fieldTopBanner = Context.loadSetting(PreviewFrameSettings.FIELD_TOP_BANNER, null);
		fieldBottomBanner = Context.loadSetting(PreviewFrameSettings.FIELD_BOTTOM_BANNER, null);
		fieldLeftBanner = Context.loadSetting(PreviewFrameSettings.FIELD_LEFT_BANNER, null);
		fieldRightBanner = Context.loadSetting(PreviewFrameSettings.FIELD_RIGHT_BANNER, null);
	}

	private Integer loadIntegerField(String field) {
		String setting = Context.loadSetting(field, "0");
		try {
			return Integer.valueOf(setting);
		} catch (Exception e) {
			return 0;
		}
	}

	public void saveSettings() throws IOException {
		logger.info("Saving settings");
		Context.saveSetting(PreviewFrameSettings.FIELD_NAME_LOCATION, fieldNameLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_SUBEVENT_LOCATION, fieldSubeventLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_CATEGORY_LOCATION, fieldCategoryLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_AGE_LOCATION, fieldAgeLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_GENDER_LOCATION, fieldGenderLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_BIRTHDATE_LOCATION, fieldBirthdateLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_LABEL_COLOR_LOCATION, fieldLabelColorLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_TEAM_LOCATION, fieldTeamLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_PROCEDENCE_LOCATION, fieldProcedenceLocation.toString());
		Context.saveSetting(PreviewFrameSettings.FIELD_TOP_BANNER, fieldTopBanner);
		Context.saveSetting(PreviewFrameSettings.FIELD_BOTTOM_BANNER, fieldBottomBanner);
		Context.saveSetting(PreviewFrameSettings.FIELD_LEFT_BANNER, fieldLeftBanner);
		Context.saveSetting(PreviewFrameSettings.FIELD_RIGHT_BANNER, fieldRightBanner);
		Context.flushSettings();
	}

	public Integer getFieldNameLocation() {
		return fieldNameLocation;
	}

	public void setFieldNameLocation(Integer fieldNameLocation) {
		this.fieldNameLocation = fieldNameLocation;
	}

	public Integer getFieldSubeventLocation() {
		return fieldSubeventLocation;
	}

	public void setFieldSubeventLocation(Integer fieldSubeventLocation) {
		this.fieldSubeventLocation = fieldSubeventLocation;
	}

	public Integer getFieldCategoryLocation() {
		return fieldCategoryLocation;
	}

	public void setFieldCategoryLocation(Integer fieldCategoryLocation) {
		this.fieldCategoryLocation = fieldCategoryLocation;
	}

	public Integer getFieldAgeLocation() {
		return fieldAgeLocation;
	}

	public void setFieldAgeLocation(Integer fieldAgeLocation) {
		this.fieldAgeLocation = fieldAgeLocation;
	}

	public Integer getFieldGenderLocation() {
		return fieldGenderLocation;
	}

	public void setFieldGenderLocation(Integer fieldGenderLocation) {
		this.fieldGenderLocation = fieldGenderLocation;
	}

	public Integer getFieldBirthdateLocation() {
		return fieldBirthdateLocation;
	}

	public void setFieldBirthdateLocation(Integer fieldBirthdateLocation) {
		this.fieldBirthdateLocation = fieldBirthdateLocation;
	}

	public Integer getFieldLabelColorLocation() {
		return fieldLabelColorLocation;
	}

	public void setFieldLabelColorLocation(Integer fieldLabelColorLocation) {
		this.fieldLabelColorLocation = fieldLabelColorLocation;
	}

	public Integer getFieldTeamLocation() {
		return fieldTeamLocation;
	}

	public void setFieldTeamLocation(Integer fieldTeamLocation) {
		this.fieldTeamLocation = fieldTeamLocation;
	}

	public Integer getFieldProcedenceLocation() {
		return fieldProcedenceLocation;
	}

	public void setFieldProcedenceLocation(Integer fieldProcedenceLocation) {
		this.fieldProcedenceLocation = fieldProcedenceLocation;
	}

	public String getFieldTopBanner() {
		return fieldTopBanner;
	}

	public String getFieldBottomBanner() {
		return fieldBottomBanner;
	}

	public String getFieldLeftBanner() {
		return fieldLeftBanner;
	}

	public String getFieldRightBanner() {
		return fieldRightBanner;
	}

	public void setFieldTopBanner(String fieldTopBanner) {
		this.fieldTopBanner = fieldTopBanner;
	}

	public void setFieldBottomBanner(String fieldBottomBanner) {
		this.fieldBottomBanner = fieldBottomBanner;
	}

	public void setFieldLeftBanner(String fieldLeftBanner) {
		this.fieldLeftBanner = fieldLeftBanner;
	}

	public void setFieldRightBanner(String fieldRightBanner) {
		this.fieldRightBanner = fieldRightBanner;
	}

}
