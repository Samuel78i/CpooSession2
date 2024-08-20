package org.devWork;

import org.devWork.resource.*;
import org.exec.resource.Resource;
import org.exec.task.Task;

import java.util.List;

public record ProjectCreation(List<Resource> resourceList, List<Task> taskList) {

    public static ProjectCreation createDevTask() {
        Analyst analyste = new Analyst();
        Architect architecte = new Architect();
        Developer developpeur1 = new Developer();
        Developer developpeur2 = new Developer();
        Developer developpeur3 = new Developer();
        Designer designer = new Designer();
        Tester testeur1 = new Tester();
        Tester testeur2 = new Tester();
        DevOps devOps = new DevOps();


        List<Resource> resources = List.of(analyste, architecte, developpeur1, developpeur2, developpeur3, designer, testeur1, testeur2, devOps, new PcSpecialDevOps());

        // Création des tâches
        Task analyseBesoins = new Task("Analyse des besoins", 5);
        analyseBesoins.addResource(analyste);

        Task conceptionArchitecture = new Task("Conception de l'architecture", 7);
        conceptionArchitecture.addDependency(analyseBesoins);
        conceptionArchitecture.addResource(architecte);

        Task miseEnPlaceEnvironnement = new Task("Mise en place de l'environnement de développement", 2);
        miseEnPlaceEnvironnement.addDependency(conceptionArchitecture);
        miseEnPlaceEnvironnement.addResource(developpeur1);

        Task devModuleUtilisateurs = new Task("Développement du module de gestion des utilisateurs", 10);
        devModuleUtilisateurs.addDependency(miseEnPlaceEnvironnement);
        devModuleUtilisateurs.addResource(developpeur2);

        Task devModuleTaches = new Task("Développement du module de gestion des tâches", 12);
        devModuleTaches.addDependency(miseEnPlaceEnvironnement);
        devModuleTaches.addResource(developpeur3);

        Task devUI = new Task("Développement de l'interface utilisateur (UI)", 15);
        devUI.addDependency(miseEnPlaceEnvironnement);
        devUI.addResource(developpeur1);
        devUI.addResource(designer);

        Task integrationModules = new Task("Intégration des modules", 5);
        integrationModules.addDependency(devModuleUtilisateurs);
        integrationModules.addDependency(devModuleTaches);
        integrationModules.addDependency(devUI);
        integrationModules.addResource(developpeur1);
        integrationModules.addResource(developpeur2);
        integrationModules.addResource(developpeur3);

        Task testsQA = new Task("Tests et assurance qualité", 10);
        testsQA.addDependency(integrationModules);
        testsQA.addResource(testeur1);
        testsQA.addResource(testeur2);

        Task deploiement = new Task("Déploiement", 3);
        deploiement.addDependency(testsQA);
        deploiement.addResource(developpeur1);
        deploiement.addResource(devOps);

        List<Task> tasks = List.of(
                analyseBesoins, conceptionArchitecture, miseEnPlaceEnvironnement,
                devModuleUtilisateurs, devModuleTaches, devUI, integrationModules,
                testsQA, deploiement);

        return new ProjectCreation(resources, tasks);
    }
}
