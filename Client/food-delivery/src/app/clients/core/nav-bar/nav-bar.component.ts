import { Component } from "@angular/core";

@Component({
    selector: 'nav-bar-root',
    standalone: true,
    templateUrl: './nav-bar.component.html',
    styleUrl: './nav.bar.component.css'
})
export class NavigationBarComponent{
    isLoggedIn = true;
}