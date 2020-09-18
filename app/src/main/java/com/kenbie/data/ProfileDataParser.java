package com.kenbie.data;

import android.content.SharedPreferences;

import com.kenbie.R;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileDataParser {
    private SharedPreferences mPref;

    // Profile info
    public ProfileInfo parseUserProfileData(String data, SharedPreferences mPref) {
        this.mPref = mPref;
        ProfileInfo profileInfo = new ProfileInfo();
        Utility utility = new Utility();
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("profile_info")) {
                JSONObject jp = new JSONObject(jo.getString("profile_info"));
                profileInfo.setId(jp.getInt("id"));
                profileInfo.setFirst_name(jp.getString("first_name"));
                profileInfo.setUser_name(jp.getString("user_name"));
                profileInfo.setSocial_id(jp.getString("social_id"));
                profileInfo.setSurname(jp.getString("surname"));
                profileInfo.setUser_type(jp.getString("user_type"));
                profileInfo.setUser_pic(jp.getString("user_pic"));
                profileInfo.setEmail_id(jp.getString("email_id"));

                try {
                    if (jp.getString("birth_year") != null && !jp.getString("birth_year").equalsIgnoreCase("null") && jp.getString("birth_year").length() > 0)
                        profileInfo.setBirth_year(jp.getString("birth_year"));

                    if (jp.getString("birth_month") != null && !jp.getString("birth_month").equalsIgnoreCase("null") && jp.getString("birth_month").length() > 0)
                        profileInfo.setBirth_month(jp.getString("birth_month"));

                    if (jp.getString("birth_day") != null && !jp.getString("birth_day").equalsIgnoreCase("null") && jp.getString("birth_day").length() > 0)
                        profileInfo.setBirth_day(jp.getString("birth_day"));

                    profileInfo.setCompletedYrs(utility.getYearsCountFromDate(profileInfo.getBirth_year(), profileInfo.getBirth_month(), profileInfo.getBirth_day()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                profileInfo.setCompany_name(jp.getString("company_name"));
                profileInfo.setPhone(jp.getString("phone"));
                profileInfo.setWebsite(jp.getString("website"));
                profileInfo.setSeen_type(jp.getString("seen_type"));
                profileInfo.setDiscipline(jp.getString("discipline"));
                profileInfo.setSocialType(jp.getString("social_type"));

                if (jp.getString("gender") != null)
                    if (jp.getString("gender").equalsIgnoreCase(mPref.getString("31", "Male")))
                        profileInfo.setGender(1);
                    else
                        profileInfo.setGender(2);

                profileInfo.setIs_active(jp.getInt("is_active"));
                profileInfo.setIs_deleted(jp.getInt("is_deleted"));
                profileInfo.setAdded_by(jp.getInt("added_by"));
                profileInfo.setUser_from_agency(jp.getString("user_from_agency"));
                profileInfo.setAbout_user(jp.getString("about_user"));
//                String[] sData = {"Ethnicity", "Gender", "Model", "Agency", "Height", "Eyes", "Hair", "Bust", "Waist", "Hips", "Dress", "Shoes"};

                ArrayList<String> userInfo = new ArrayList<>();
                profileInfo.setUser_ethinicity(jp.getString("user_ethinicity"));
                userInfo.add(jp.getString("user_ethinicity"));
                if (profileInfo.getUser_type() != null && profileInfo.getUser_type().equalsIgnoreCase("1"))
                    userInfo.add(profileInfo.getGender() == 1 ? mPref.getString("31", "Male") : mPref.getString("32", "Female"));
                else
                    userInfo.add(null);

                profileInfo.setUser_face(jp.getString("user_face"));
                userInfo.add(jp.getString("user_face"));

                if (profileInfo.getUser_type() != null && profileInfo.getUser_type().equalsIgnoreCase("1"))
                    if (profileInfo.getUser_from_agency() != null && profileInfo.getUser_from_agency().equalsIgnoreCase("Yes"))
                        userInfo.add(mPref.getString("21", "Yes"));
                    else
                        userInfo.add(null);
                else
                    userInfo.add(null);

//                userInfo.add(setUserType(jp.getString("user_type")));
                profileInfo.setUser_height(jp.getString("user_height"));
                userInfo.add(jp.getString("user_height"));
                profileInfo.setUser_eye_color(jp.getString("user_eye_color"));
                userInfo.add(jp.getString("user_eye_color"));
                profileInfo.setUser_hair_color(jp.getString("user_hair_color"));
                userInfo.add(jp.getString("user_hair_color"));
                profileInfo.setUser_bust(jp.getString("user_bust"));
                userInfo.add(jp.getString("user_bust"));
                profileInfo.setUser_waist(jp.getString("user_waist"));
                userInfo.add(jp.getString("user_waist"));
                profileInfo.setUser_hips(jp.getString("user_hips"));
                userInfo.add(jp.getString("user_hips"));
                profileInfo.setUser_dress(jp.getString("user_dress"));
                userInfo.add(jp.getString("user_dress"));
                profileInfo.setUser_shoes(jp.getString("user_shoes"));
                userInfo.add(jp.getString("user_shoes"));
                profileInfo.setUser_weight(jp.getString("user_weight"));

                if (!jp.getString("selected_lang").equalsIgnoreCase("null"))
                    profileInfo.setUserSelLanguage(jp.getString("selected_lang"));
                profileInfo.setUser_categories(jp.getString("user_categories"));
                profileInfo.setUser_disciplines(jp.getString("user_disciplines"));
                profileInfo.setUserInfo(bindUserInfo(userInfo));

                profileInfo.setAddress1(jp.getString("address1"));
                profileInfo.setAddress2(jp.getString("address2"));
                profileInfo.setCountry(jp.getString("country"));
                profileInfo.setCity(jp.getString("city"));
                profileInfo.setPostal_code(jp.getString("postal_code"));

                ArrayList<String> socialDetails = new ArrayList<>();

                profileInfo.setFb(jp.getString("fb"));
                socialDetails.add(jp.getString("fb"));

                profileInfo.setInsta(jp.getString("insta"));
                socialDetails.add(jp.getString("insta"));

                profileInfo.setTwitter(jp.getString("twitter"));
                socialDetails.add(jp.getString("twitter"));
//                profileInfo.setGoogle(jp.getString("google"));
//                socialDetails.add(jp.getString("google"));
//                profileInfo.setYandex(jp.getString("yandex"));
//                socialDetails.add(jp.getString("yandex"));

//                profileInfo.setYoutube(jp.getString("youtube"));
//                socialDetails.add(jp.getString("youtube"));
//                profileInfo.setLinkedin(jp.getString("linkedin"));
//                socialDetails.add(jp.getString("linkedin"));
                profileInfo.setPinterest(jp.getString("pinterest"));
                socialDetails.add(jp.getString("pinterest"));

//                if (jp.has("website")) {
//                    profileInfo.setSocialWebsite(jp.getString("website"));
//                    socialDetails.add(jp.getString("website"));
//                }
                profileInfo.setUserSocial(bindSocialLink(socialDetails));
                profileInfo.setUserLanguages(getMyLanguageData(jp.getString("user_language")));
            }

            if (jo.has("userCountData"))
                profileInfo.setUserCountData(jo.getString("userCountData"));
            profileInfo.setAllimgs(jo.getString("allimgs"));

            profileInfo.setList_options(jo.getString("list_options"));
            JSONObject listOptions = new JSONObject(profileInfo.getList_options());
            profileInfo.setCategoriesData(listOptions.getString("categories"));
            profileInfo.setArrdisciplines(listOptions.getString("arrdisciplines"));
            profileInfo.setUserDisciplines(getMyData(listOptions.getString("arrdisciplines"), profileInfo.getUser_disciplines()));
            profileInfo.setUserCategories(getMyData(listOptions.getString("categories"), profileInfo.getUser_categories()));
            profileInfo.setUser_language(listOptions.getString("lang_list"));
            profileInfo.setGalleryList(getGalleryImages(profileInfo.getAllimgs()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileInfo;
    }

    private String getUserLanguageCode(String language) {
        String value = "";
        try {
            JSONArray jsonArray = new JSONArray(language);
            for (int i = 0; i < jsonArray.length(); i++) {
                value = jsonArray.getJSONObject(i).getString("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private String setUserType(String user_type) {
        String userType = "";
        try {
            int value = Integer.valueOf(user_type);
            if (value == 1)
                userType = "Model";
            else if (value == 2)
                userType = "Photographer";
            else
                userType = "Agency";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userType;
    }

    // Language data
    private ArrayList<OptionsData> getMyLanguageData(String user_language) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            if (user_language != null && !user_language.equalsIgnoreCase("null") && user_language.length() > 0) {
                JSONArray ja = new JSONArray(user_language);
                for (int i = 0; i < ja.length(); i++) {
                    OptionsData od = new OptionsData();
                    od.setId((i + 1));
                    od.setName(ja.getJSONObject(i).getString("name"));
                    od.setOptionCode(ja.getJSONObject(i).getString("id"));
                    values.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    // Gallery images
    private ArrayList<OptionsData> getGalleryImages(String allimgs) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            if (allimgs != null && allimgs.length() > 10) {
                JSONArray imageArray = new JSONArray(allimgs);

                for (int i = 0; i < imageArray.length(); i++) {
                    OptionsData value = new OptionsData();
                    value.setId((i + 1));
                    value.setName(imageArray.getString(i));
                    values.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }


    // Bind user info
    private ArrayList<OptionsData> bindUserInfo(ArrayList<String> userInfo) {
//        String[] sData = {"Ethnicity", "Gender", "Model", "Agency", "Height", "Eyes", "Hair", "Bust", "Waist", "Hips", "Dress", "Shoes"};
        String[] sData = {mPref.getString("92", "Ethnicity"), mPref.getString("69", "Gender"), mPref.getString("28", "Model"), mPref.getString("30", "Agency"), mPref.getString("83", "Height"), mPref.getString("213", "Eyes"), mPref.getString("214", "Hair"), mPref.getString("86", "Bust"), mPref.getString("87", "Waist"), mPref.getString("88", "Hips"), mPref.getString("89", "Dress"), mPref.getString("215", "Shoes")};

        ArrayList<OptionsData> values = new ArrayList<>();
        for (int i = 0; i < sData.length; i++) {
            OptionsData od = new OptionsData();
            od.setId(i + 1);
            od.setName(sData[i]);
            String value = userInfo.get(i);
            if (value != null && !value.equalsIgnoreCase("null") && value.length() > 0) {
                od.setOptionData(userInfo.get(i));
                values.add(od);
            }
        }
        return values;
    }

    // Binding social data
    private ArrayList<OptionsData> bindSocialLink(ArrayList<String> socialDetails) {
        ArrayList<OptionsData> values = new ArrayList<>();
//        String[] sData = {"Facebook", "Instagram", "Twitter", "Yandex", "YouTube", "Pinterest"};
//        String[] sData = {mPref.getString("242", "Facebook"), mPref.getString("243", "Instagram"), mPref.getString("244", "Twitter"), mPref.getString("247", "Pinterest")};
//        Integer[] sImages = {R.drawable.is_fb, R.drawable.ic_instagram, R.drawable.is_twitter, R.drawable.is_pinterest};

        String[] sData = {mPref.getString("242", "Facebook"), mPref.getString("243", "Instagram")};
        Integer[] sImages = {R.drawable.ic_facebook_profile, R.drawable.ic_insta_profile};
        try {
            for (int i = 0; i < sData.length; i++) {
                OptionsData od = new OptionsData();
                od.setId((i + 1));
                od.setName(sData[i]);
                od.setImgId(sImages[i]);
                if (socialDetails.get(i) != null && socialDetails.get(i).length() > 0 && !socialDetails.get(i).equalsIgnoreCase("null")) {
                    od.setOptionData(socialDetails.get(i));
                    od.setActive(true);
                    values.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    private ArrayList<OptionsData> getMyData(String options, String myData) {
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<OptionsData> optionsDataEthnicity = new ArrayList<>();
        try {
            if (myData != null && !myData.equalsIgnoreCase("null")) {
                String[] mData = myData.replace(",", "-").split("-");
                if (mData != null)
                    for (int i = 0; i < mData.length; i++)
                        try {
                            values.add(Integer.valueOf(mData[i]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

            }

            JSONArray userEthnicity = new JSONArray(options);
            for (int i = 0; i < userEthnicity.length(); i++) {
                OptionsData od = new OptionsData();
                JSONObject jod = new JSONObject(userEthnicity.getString(i));
                od.setId(jod.getInt("id"));
                od.setName(jod.getString("name"));
                if (jod.has("shortname"))
                    od.setOptionCode(jod.getString("shortname"));

                if (values.indexOf(od.getId()) != -1) {
                    od.setActive(true);
                    optionsDataEthnicity.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionsDataEthnicity;
    }
}
