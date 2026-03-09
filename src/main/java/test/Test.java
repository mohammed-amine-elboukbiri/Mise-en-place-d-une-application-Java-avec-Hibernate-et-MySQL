package test;

import entities.Machine;
import entities.Salle;
import services.MachineService;
import services.SalleService;

import java.util.Date;

public class Test {

    public static void main(String[] args) {
        SalleService salleService = new SalleService();
        MachineService machineService = new MachineService();

        // Création et insertion de salles
        Salle salle1 = new Salle("A198");
        Salle salle2 = new Salle("B8837");
        salleService.create(salle1);
        salleService.create(salle2);

        // Création et insertion de machines
        Machine machine1 = new Machine("Machine de service A1", new Date(), salleService.findById(1));
        Machine machine2 = new Machine("machine de service A2 ", new Date(), salleService.findById(2));
        machineService.create(machine1);
        machineService.create(machine2);

        // Affichage des salles et leurs machines
        for(Salle salle : salleService.findAll()) {
            System.out.println("Salle: " + salle.getCode());
            for(Machine machine : salle.getMachines()) {
                System.out.println("  Machine: " + machine.getRef());
            }
        }

        // Utilisation de la méthode findBetweenDate
        Date d1 = new Date(120, 0, 1); // 1er janvier 2020
        Date d2 = new Date(); // Date actuelle
        System.out.println("Machines achetées entre " + d1 + " et " + d2 + ":");
        for(Machine m : machineService.findBetweenDate(d1, d2)) {
            System.out.println(m.getRef() + " achetée le " + m.getDateAchat());
        }
    }
}