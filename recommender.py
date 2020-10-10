#!/usr/bin/env python
# coding: utf-8
import json
from importlib import reload
import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import user_profiles as up
pd.options.mode.chained_assignment = None
db=up.firebase.database()

# Calculer la distance entre l'utilisateur et le restaurant en utilistant Haversine Distance
def haversine_distance(lat1, lon1, lat2, lon2):

    r = 6371  # Rayon de la terre

    phi1 = np.radians(lat1)

    phi2 = np.radians(lat2)

    delta_phi = np.radians(lat2 - lat1)

    delta_lambda = np.radians(lon2 - lon1)

    a = np.sin(delta_phi / 2) ** 2 + np.cos(phi1) * np.cos(phi2) * np.sin(delta_lambda / 2) ** 2

    res = r * (2 * np.arctan2(np.sqrt(a), np.sqrt(1 - a)))

    return np.round(res, 2)


# Avoir toutes les distances entre l'utilisateur et le restaurant
def get_restaurants_distances(restaurants=up.restos):

    lat = latitude

    long = longitude

    distances = []

    rest = []

    # Parcourir tous les restaurants

    for restaurant in range(len(restaurants)):

        rlatitude = restaurants.loc[restaurant].latitude  # Lattitude du restaurant

        rlongitude = restaurants.loc[restaurant].longitude  # Longitude du restaurant

        distance = haversine_distance(lat, long, rlatitude, rlongitude)  # Calculer la distance

        distances.append(distance)

        rest.append(restaurants.loc[restaurant].title)

        restaurants_distances = pd.DataFrame({'Restaurant': rest, 'Distance': distances})

    return restaurants_distances


# Avoir les similarités entre l'utilisateur et les restaurants
def get_similarities(profil_utilisateur, profil_restaurants):

    distances = get_restaurants_distances()  # Avoir les distances

    df_combined = profil_restaurants.append(profil_utilisateur).reset_index().fillna(0)  # Pour calculer la similarité entre l'utilisateur et les restaurants

    df_combined = df_combined.drop('index', axis=1)

    df_final = pd.DataFrame(cosine_similarity(df_combined), index=df_combined.index,columns=df_combined.index)  # Calculer la similarité

    np.fill_diagonal(df_final.values, 0)

    simtest = df_final.loc[len(df_final) - 1]  # Avoir le tableau de similarités des restaurants de l'utilisateur

    final = distances.join(simtest)  # Avoir le tableau des similarités+Distances

    final = final.rename(columns={final.columns[2]: "Similarity"})

    return final


# Recommander à l'utilisateur
def recommend(profil_restaurants, profil_utilisateur):

    alpha = a

    beta = b

    data = get_similarities(profil_utilisateur, profil_restaurants)  # Générer le Dataframe du score

    data['score'] = alpha * data['Similarity'] + beta * (1 - (data["Distance"] / 10))  # Score entre 0 et 1

    return data.sort_values(by="score", ascending=False).head(50)


# Restaurants à proximité
def get_nearbyrestaurants(lat,long):

    reload(up) #Permet d'actualiser les paramètres d'entrée en cas de changement au niveau de Firebase

    global latitude,longitude

    latitude=lat

    longitude=long

    distances = get_restaurants_distances()

    distances=distances[distances.Distance<=10].sort_values(by="Distance",ascending=True)

    liste=[]

    for i in range(len(distances)):

        liste.append(int(distances.index[i])) #Créer un ArrayList des ID restaurants à recommander

    liste_finale = {"output": liste}

    return json.dumps(liste_finale)


# Quand un utilisateur visite un restaurant et a attribué la note, les poids changent en fonction du restaurant:
##Si le restaurant est plus proche que similaire alors le poids beta va être plus grand
def update_weights(user,restaurant_id,lat,long):

    reload(up) #Permet d'actualiser les paramètres d'entrée en cas de changement au niveau de Firebase

    global latitude,longitude #Variables globales

    latitude=lat #Latitude de l'utilisateur

    longitude=long #Longiude de l'utilisateur

    user_profile,restos_profiles,alpha,beta=up.creer_profil_utilisateur(user) #Génération du profil utilisateur, profil restaurant, alpha (poids de similarité) beta (poids de distance)

    final=get_similarities(user_profile,restos_profiles) #Calculer la similarité et la distance entre l'utilisateur actif et tous les restaurants

    new_alpha=alpha+0.1*(final.loc[restaurant_id]['Similarity']-(1-(final.loc[restaurant_id]['Distance']/10))) #Calcul de la nouvelle valeur de Alpha de l'utilsateur

    new_beta=beta+0.1*((1-(final.loc[restaurant_id]['Distance']/10))-final.loc[restaurant_id]['Similarity']) #Calcul de la nouvelle valeur de Beta de l'utilsateur

    if not (new_alpha < 0 or new_beta < 0):

        db.child('users').child(user).update({"alpha": str(new_alpha)}) #Écraser la valeur de Alpha dans Firebase par sa nouvelle valeur après la notation de l'utilisateur

        db.child('users').child(user).update({"beta": str(new_beta)}) #Écraser la valeur de Beta dans Firebase par sa nouvelle valeur après la notation de l'utilisateur


# Liste des IDs de restaurants à recommander
def get_output(user, lat, lon):

    reload(up) #Permet d'actualiser les paramètres d'entrée en cas de changement au niveau de Firebase

    #Déclarées comme variables globales pour les utiliser dans d'autres fonctions:
    # Latitude et longitude récupérées depuis la requête HTTP, a et b les poids récupérés à partir de Firebase
    global latitude, longitude, a, b

    latitude = lat  # Récupérer par requête HTTP

    longitude = lon  # Récupérer par requête HTTP

    liste = [] # Initialisation de la liste des ID restaurants à recommander

    user_profile,restos_profiles,a,b = up.creer_profil_utilisateur(user) #Génération du profil utilisateur, profil restaurant, a=alpha (poids de similarité) b=beta (poids de distance)

    recommendation = recommend(restos_profiles, user_profile) #Génération du DataFrame de recommandation

    for i in range(len(recommendation)):

        liste.append(int(recommendation.index[i])) #Créer un ArrayList des ID restaurants à recommander

    liste_finale = {"output": liste} #Création du JSON de Recommandation avec un seul attribut: output et comme paramètres: liste

    return json.dumps(liste_finale) # Retourner la recommandation sous forat JSON


def ratings():

    reload(up)

    all_restos=pd.DataFrame(index=up.restos.index)

    mean_ratings=pd.merge(pd.DataFrame(up.rating_matrix.mean(axis=0)),pd.DataFrame(up.rating_matrix.count(axis=0)),left_index=True,right_index=True)

    mean_ratings=mean_ratings.rename(columns={mean_ratings.columns[0]: "Notes",mean_ratings.columns[1]: "Nombre"})

    mean=pd.merge(all_restos,mean_ratings,left_index=True,right_index=True,how='outer').fillna(0)

    mean['restoID'] = mean.index

    mean['Nombre']=mean['Nombre'].astype(int)

    mean=mean.astype(str)

    return mean