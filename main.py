import tkinter as tk
from tkinter import messagebox
import random
import time
from datetime import datetime, timedelta

class MysteryGame:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Who's the Culprit? - Detective Game")
        self.root.geometry("800x600")
        self.root.configure(bg='#2c3e50')
        
        # Game variables
        self.time_left = 1800  # 30 minutes in seconds
        self.found_clues = []
        self.suspects = ["Butler", "Maid", "Chef"]
        self.clues = [
            "Fingerprint on the knife",
            "Muddy footprints near the window", 
            "Broken vase in the hallway",
            "Suspicious alibi from the butler",
            "Blood stain on the carpet"
        ]
        self.killer = random.choice(self.suspects)
        self.game_started = False
        
        self.setup_ui()
        self.update_timer()
    
    def setup_ui(self):
        # Title
        title_label = tk.Label(
            self.root, 
            text="🕵️ WHO'S THE CULPRIT? 🕵️", 
            font=("Arial", 24, "bold"),
            bg='#2c3e50',
            fg='#ecf0f1'
        )
        title_label.pack(pady=20)
        
        # Timer
        self.timer_label = tk.Label(
            self.root,
            text="Time Left: 30:00",
            font=("Arial", 16),
            bg='#2c3e50',
            fg='#e74c3c'
        )
        self.timer_label.pack(pady=10)
        
        # Start button
        self.start_button = tk.Button(
            self.root,
            text="START INVESTIGATION",
            font=("Arial", 14, "bold"),
            bg='#27ae60',
            fg='white',
            command=self.start_game,
            height=2,
            width=20
        )
        self.start_button.pack(pady=20)
        
        # Game area
        self.game_frame = tk.Frame(self.root, bg='#2c3e50')
        self.game_frame.pack(pady=20, fill='both', expand=True)
        
        # Clues found
        self.clues_label = tk.Label(
            self.game_frame,
            text="Clues Found: 0/5",
            font=("Arial", 12),
            bg='#2c3e50',
            fg='#f39c12'
        )
        self.clues_label.pack()
        
        # Action buttons
        self.buttons_frame = tk.Frame(self.game_frame, bg='#2c3e50')
        self.buttons_frame.pack(pady=20)
        
        # Suspect buttons
        self.suspect_buttons = []
        for suspect in self.suspects:
            btn = tk.Button(
                self.buttons_frame,
                text=f"Interrogate {suspect}",
                font=("Arial", 12),
                bg='#3498db',
                fg='white',
                command=lambda s=suspect: self.interrogate_suspect(s),
                width=15,
                height=2
            )
            btn.pack(side=tk.LEFT, padx=10)
            self.suspect_buttons.append(btn)
        
        # Search for clues button
        self.search_button = tk.Button(
            self.game_frame,
            text="🔍 Search for Clues",
            font=("Arial", 14, "bold"),
            bg='#e67e22',
            fg='white',
            command=self.search_clues,
            width=20,
            height=2
        )
        self.search_button.pack(pady=20)
        
        # Make accusation button
        self.accuse_button = tk.Button(
            self.game_frame,
            text="🚨 MAKE ACCUSATION",
            font=("Arial", 16, "bold"),
            bg='#e74c3c',
            fg='white',
            command=self.make_accusation,
            width=25,
            height=3
        )
        self.accuse_button.pack(pady=20)
        
        # Initially hide game elements
        self.hide_game_elements()
    
    def hide_game_elements(self):
        for widget in self.game_frame.winfo_children():
            widget.pack_forget()
    
    def show_game_elements(self):
        self.clues_label.pack()
        self.buttons_frame.pack(pady=20)
        self.search_button.pack(pady=20)
        self.accuse_button.pack(pady=20)
    
    def start_game(self):
        self.game_started = True
        self.start_button.pack_forget()
        self.show_game_elements()
        messagebox.showinfo("Game Started", f"Investigation begins! The killer is {self.killer} (shh, don't tell anyone!)")
    
    def update_timer(self):
        if self.game_started and self.time_left > 0:
            minutes = self.time_left // 60
            seconds = self.time_left % 60
            self.timer_label.config(text=f"Time Left: {minutes:02d}:{seconds:02d}")
            self.time_left -= 1
            
            if self.time_left <= 0:
                self.game_over("Time's up! The killer got away!")
        
        self.root.after(1000, self.update_timer)
    
    def search_clues(self):
        if len(self.found_clues) < 5:
            available_clues = [clue for clue in self.clues if clue not in self.found_clues]
            if available_clues:
                found_clue = random.choice(available_clues)
                self.found_clues.append(found_clue)
                self.clues_label.config(text=f"Clues Found: {len(self.found_clues)}/5")
                messagebox.showinfo("Clue Found!", f"You found: {found_clue}")
            else:
                messagebox.showinfo("No More Clues", "You've found all the clues!")
        else:
            messagebox.showinfo("All Clues Found", "You've collected all the clues!")
    
    def interrogate_suspect(self, suspect):
        if suspect == self.killer:
            messagebox.showinfo("Suspicious!", f"{suspect} seems nervous and gives conflicting answers...")
        else:
            messagebox.showinfo("Alibi", f"{suspect} has a solid alibi and seems innocent.")
    
    def make_accusation(self):
        if len(self.found_clues) < 3:
            messagebox.showwarning("Not Enough Evidence", "You need at least 3 clues before making an accusation!")
            return
        
        # Create accusation dialog
        accusation_window = tk.Toplevel(self.root)
        accusation_window.title("Make Your Accusation")
        accusation_window.geometry("400x300")
        accusation_window.configure(bg='#2c3e50')
        
        tk.Label(
            accusation_window,
            text="Who do you think is the killer?",
            font=("Arial", 16, "bold"),
            bg='#2c3e50',
            fg='white'
        ).pack(pady=20)
        
        for suspect in self.suspects:
            btn = tk.Button(
                accusation_window,
                text=f"Accuse {suspect}",
                font=("Arial", 12, "bold"),
                bg='#e74c3c',
                fg='white',
                command=lambda s=suspect: self.check_accusation(s, accusation_window),
                width=15,
                height=2
            )
            btn.pack(pady=10)
    
    def check_accusation(self, accused, window):
        window.destroy()
        if accused == self.killer:
            self.game_over("🎉 CONGRATULATIONS! You caught the killer!")
        else:
            self.game_over(f"❌ Wrong! The real killer was {self.killer}")
    
    def game_over(self, message):
        self.game_started = False
        messagebox.showinfo("Game Over", message)
        self.root.quit()
    
    def run(self):
        self.root.mainloop()

if __name__ == "__main__":
    game = MysteryGame()
    game.run() 