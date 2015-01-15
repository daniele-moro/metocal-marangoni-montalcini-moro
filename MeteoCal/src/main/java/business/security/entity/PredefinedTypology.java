/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.entity;

public enum PredefinedTypology {
    
    aperitif,
    dinner,
    lunch,
    breakfast,
    baptism,
    communion,
    confirmation,
    marriage,
    graduation,
    party,
    birthday,
    sport;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
    
    
    
}